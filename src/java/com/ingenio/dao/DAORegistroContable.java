package com.ingenio.dao;

import com.ingenio.excepciones.ExcepcionGeneral;
import com.ingenio.objetos.RegistroContableDetalle;
import com.ingenio.objetos.RegistroContableEncabezado;
import com.ingenio.utilidades.Utilidades;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DAORegistroContable extends DAOGenerales {

    public final String OBJETO = "REGISTROCONTABLE";
    private Connection conexion = null;
    private PreparedStatement sentencia = null;
    private ResultSet resultado = null;
    private static final Logger LOG = Logger.getLogger(DAORegistroContable.class.getName());

    public void crearEncabezado(RegistroContableEncabezado registro) throws ExcepcionGeneral {
        try {
            setConsulta("select * from fn_regconenc_ins(?,?,?,?,?)");
            conexion = getConexion();
            sentencia = conexion.prepareStatement(getConsulta());
            sentencia.setShort(1, registro.getDocumento().getId_documento());
            sentencia.setDate(2, new java.sql.Date(registro.getFechaMovimiento().getTimeInMillis()));
            sentencia.setString(3, registro.getComentario());
            sentencia.setShort(4, registro.getPeriodo().getId_periodo_contable());
            sentencia.setShort(5, registro.getUsuario().getIdusuario());
            resultado = sentencia.executeQuery();
            if (resultado.next()) {
                registro.setId_reg_con_enc(resultado.getShort("id_reg_con_enc"));
                registro.getDocumento().setConsecutivo(resultado.getInt("consecutivo"));
                registro.setFechaCreacion(resultado.getDate("fecha_creacion"));
            }
        } catch (SQLException sqle) {
            Utilidades.get().generaLogServer(LOG, Level.SEVERE, "Error en DAORegistroContable.crear: {0}", new Object[]{sqle.getMessage()});
            throw new ExcepcionGeneral(sqle.getMessage());
        } finally {
            cierraConexion(conexion, sentencia, resultado);
        }
    }

    public void registrarDocumentoCompleto(RegistroContableEncabezado encabezado,
                                            List<RegistroContableDetalle> detalle)
                                            throws ExcepcionGeneral {
        try {
            crearEncabezado(encabezado);
            
            if (encabezado.getId_reg_con_enc() > 0) {
                
                setConsulta("select fn_regcondet_ins(?,?,?,?,?,?)");
                conexion = getConexion();
                sentencia = conexion.prepareStatement(getConsulta());
                
                for (RegistroContableDetalle registro : detalle) {
                    registro.setId_reg_con_enc(encabezado.getId_reg_con_enc());
                    sentencia.setInt(1, registro.getId_reg_con_enc());
                    sentencia.setShort(2, registro.getCuentaPuc().getIdcuenta());
                    sentencia.setInt(3, registro.getDebito());
                    sentencia.setInt(4, registro.getCredito());
                    sentencia.setShort(5, registro.getTercero().getId_tercero());
                    sentencia.setShort(6, registro.getCentroCosto().getId_centro_costo());
                    resultado = sentencia.executeQuery();
                }
            } else {
                throw new ExcepcionGeneral("No se pudo insertar el encabezado.");
            }
        } catch (SQLException | ExcepcionGeneral eg) {
            if(encabezado.getId_reg_con_enc() > 0){
                anularDocumento(encabezado);
            }
            Utilidades.get().generaLogServer(LOG, Level.SEVERE, "Error en DAORegistroContable.registrarDocumentoCompleto: {0}", new Object[]{eg.getMessage()});
            throw new ExcepcionGeneral(eg.getMessage());
        } finally {
            cierraConexion(conexion, sentencia, resultado);
        }
    }

    public boolean anularDocumento(RegistroContableEncabezado registro) {
        boolean respuesta = false;
        try {
            setConsulta("select fn_regconenc_anular(?,?)");
            conexion = getConexion();
            sentencia = conexion.prepareStatement(getConsulta());
            sentencia.setInt(1, registro.getId_reg_con_enc());
            sentencia.setShort(2, registro.getUsuario().getIdusuario());
            resultado = sentencia.executeQuery();
            if(resultado.next()){
                respuesta = resultado.getBoolean(1);
            }
        } catch (SQLException | ExcepcionGeneral eg) {
            Utilidades.get().generaLogServer(LOG, Level.SEVERE, "Error en DAORegistroContable.anularDocumento: {0}", new Object[]{eg.getMessage()});
            throw new ExcepcionGeneral(eg.getMessage());
        } finally {
            cierraConexion(conexion, sentencia, resultado);
        }
        return respuesta;
    }

}