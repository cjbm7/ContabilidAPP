package com.ingenio.servlets.contabilidad;

import com.ingenio.dao.DAOPeriodo;
import com.ingenio.dao.DAORegistroContableEncabezado;
import com.ingenio.objetos.DocumentoContable;
import com.ingenio.objetos.Periodo;
import com.ingenio.objetos.RegistroContableEncabezado;
import com.ingenio.objetos.Usuario;
import com.ingenio.utilidades.Constantes;
import com.ingenio.utilidades.Utilidades;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@MultipartConfig
@WebServlet(name = "SRegContableEncabezadoCrear", urlPatterns = {"/SRegContableEncabezadoCrear"})
public class SRegContableEncabezadoCrear extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(SRegContableEncabezadoCrear.class.getName());

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");

        HttpSession sesion = request.getSession();
        byte tipo;
        String mensaje;
        String objeto = "";
        boolean registroValido = false;

        if (Utilidades.get().autenticado(sesion)) {
            DAORegistroContableEncabezado dao = new DAORegistroContableEncabezado();
            DAOPeriodo daoPeriodo = new DAOPeriodo();
            
            Usuario usuario = (Usuario) sesion.getAttribute("credencial");

            if (dao.tienePermiso(usuario.getPerfil(), dao.OBJETO, Constantes.INSERTAR)) {
                String idDocumento = request.getParameter("idDocumento");
                String fecha = request.getParameter("fecha");
                String comentario = request.getParameter("comentario");

                Calendar cFecha = Utilidades.get().parseFecha(fecha, LOG, true);
                Periodo periodoContable = new Periodo();
                if (cFecha != null) {
                    int ano = cFecha.get(Calendar.YEAR);
                    int mes = cFecha.get(Calendar.MONTH) + 1;
                    periodoContable.setAnio((short) ano);
                    periodoContable.setMes((short) mes);
                    periodoContable = daoPeriodo.getPeriodoXFecha(periodoContable);
                }
                
                short iIdDocumento = Utilidades.get().parseShort(idDocumento, LOG, true);
                
                if (iIdDocumento == 0) {
                    mensaje = "El documento no es válido";
                } else if (comentario == null || comentario.length() == 0) {
                    mensaje = "El comentario no puede estar vacío.";
                } else if (periodoContable == null || periodoContable.getId_periodo_contable() == 0) {
                    mensaje = "La fecha y/o el periodo es válido.";
                } else if(!periodoContable.isAbierto()){
                    mensaje = "Este periodo contable ya está cerrado. No se pueden hacer movimientos.";
                } else {
                    mensaje = "";
                    registroValido = true;
                }
                
                if(registroValido){
                    RegistroContableEncabezado rce = new RegistroContableEncabezado();
                    DocumentoContable documento = new DocumentoContable();
                    
                    documento.setId_documento(iIdDocumento);
                    rce.setDocumento(documento);
                    rce.setFechaMovimiento(cFecha);
                    rce.setComentario(comentario);
                    rce.setPeriodo(periodoContable);
                    
                    sesion.setAttribute("registrocontableencabezado", rce);
                    tipo = Constantes.MSG_CORRECTO;
                    mensaje = "Registro contable establecido en la sesión";
                    objeto = rce.toJSON();
                } else {
                    tipo = Constantes.MSG_ERROR;
                }

            } else {
                tipo = Constantes.MSG_ADVERTENCIA;
                mensaje = Constantes.MSG_SIN_PERMISO_TEXT;
            }
        } else {
            tipo = Constantes.MSG_NO_AUTENTICADO;
            mensaje = Constantes.MSG_NO_AUTENTICADO_TEXT;
        }
        try (PrintWriter out = response.getWriter()) {
            out.println(Utilidades.get().respuestaJSON(tipo, mensaje, objeto));
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
