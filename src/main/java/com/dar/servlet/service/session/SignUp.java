package com.dar.servlet.service.session;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dar.Tools;
import com.dar.Validator;
import com.dar.backend.sql.SQLManager;
import org.json.simple.JSONObject;

public class SignUp extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect(request.getContextPath());
    }

    //TODO: proper error manager
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("login");
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String email = request.getParameter("email");
        String birth = request.getParameter("birthday");
        String country = request.getParameter("country");
        String password = request.getParameter("password");
        String securePass, salt;
        
      //Check parameters
        Validator v = new Validator();
        if(!v.firstname(firstname)) response.sendRedirect(request.getContextPath());
        if(!v.lastname(lastname)) response.sendRedirect(request.getContextPath());
        if(!v.email(email)) response.sendRedirect(request.getContextPath());
        System.out.println(new java.util.Date().toString() + "SignUp POST : PARAMS ARE OK");

        Calendar cal = Calendar.getInstance();
        PrintWriter out = response.getWriter();
        String rq = ("INSERT INTO users (login, password, salt, firstname, lastname, birthday, country, email) VALUES (?,?,?,?,?,?,?,?);");
        PreparedStatement stmt;
        Connection conn = null;
        try {
            String[] list = birth.split("-");
            cal.set(Calendar.YEAR, Integer.parseInt(list[0]));
            cal.set(Calendar.MONTH, Integer.parseInt(list[1]));
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(list[2]));
            String hashedReturn = Tools.createHash(password);
            list = hashedReturn.split(":");
            salt = list[3];
            securePass = list[4];
            SQLManager mngr = new SQLManager();
            conn = mngr.getConnection();
            stmt = conn.prepareStatement(rq);
            stmt.setString(1, username);
            stmt.setString(2, securePass);
            stmt.setString(3, salt);
            stmt.setString(4, firstname);
            stmt.setString(5, lastname);
            stmt.setDate(6, new Date(cal.getTimeInMillis()));
            stmt.setString(7, country);
            stmt.setString(8, email);
            stmt.executeUpdate();
            conn.close();
        } catch(Exception e){
        	if(conn!= null)
				try {
					if(!conn.isClosed()) conn.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
            e.printStackTrace(out);
            out.close();
            return;
        }
        //response.setContentType("application/json");
        //JSONObject object = new JSONObject();
        //object.put("status", "success");
        //out.print(object);
        //out.close();
        response.sendRedirect(request.getContextPath());
    }
}