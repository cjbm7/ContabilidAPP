"use strict";!function(n,t){function e(){var e=null,o=null,r={},i={},u=null,a={getID:function(n){return e=t.getElementById(n),this},click:function(n){return e.addEventListener("click",n,!1),this},text:function(n){return e.textContent=n,this},innerHTML:function(n){return e.innerHTML=n,this},get:function(){return e},noSubmit:function(){return e.addEventListener("submit",function(n){n.preventDefault()},!1),this},value:function(){return e.value},controlador:function(n,t){i[n]={controlador:t}},getCtrl:function(){return u},enrutar:function(){return o=e,this},ruta:function(n,t,e,o){return r[n]={plantilla:t,controlador:e,carga:o},this},manejadorRutas:function(){var t=n.location.hash.substring(1)||"/",e=r[t];new XMLHttpRequest;e&&e.plantilla?(e.controlador&&(u=i[e.controlador].controlador),this.ajax({metodo:"get",url:e.plantilla,funcion:function(){o.innerHTML=this.responseText,setTimeout(function(){"function"==typeof e.carga&&e.carga()},300)}})):n.location.hash="#/"},ajax:function(n){var t=n.metodo||"post",e=n.url||"",o=n.datos||null,r=n.funcion,i=new XMLHttpRequest;i.addEventListener("load",r,!1),i.open(t,e,!0),i.send(o)}};return a}"undefined"==typeof n.libreria?n.libreria=e():console.log("Ya está llamada")}(window,document);