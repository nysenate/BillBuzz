<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>NYSS BillBuzz</title>
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/static/img/nys_favicon_0.ico" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/static/app.css" />
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/app.js"></script>
  </head>
  <body>
    <div id="page">
      <div id="header">
        <a href="http://www.nysenate.gov">
          <img src="http://www.nysenate.gov/sites/all/themes/nys/images/nyss_logo.png" id="logo" />
        </a>
      </div>
      <div class="sub-header"></div>
      <div class="notice">
        BillBuzz is currently in "beta" and may occasionally offer incorrect data.
        Please give us your feedback at
        <a href="http://www.nysenate.gov/contact">http://www.nysenate.gov/contact</a>.
      </div>
      <div id="main" align="center">
        <h2>What is BillBuzz?</h2>
        <div class="bb_main">
          <table>
            <tr>
              <td>
                <div style="width: 650px;">
                BillBuzz is a service that allows Senators, Senate staff and
                constituents to easily see what is being said about legislation.
                <p>BillBuzz scans the Senate's <a href="http://open.nysenate.gov/legislation">OpenLegislation</a>
                website for comments left by visitors on bills. Subscribers to
                BillBuzz are sent daily emails that pull in recent comments
                made on a Senator's sponsored legislation. You can sign up to
                receive the latest 'buzz' around one or more Senator's
                sponsored legislation.
                </div>
              </td>
            </tr>
          </table>
        </div>
        <div class="nav-bar">
        <a href="<%=request.getContextPath()+"/signup/form"%>" class="nav-button-<%=request.getParameter("page").equals("signup") ? "on" : "off"%>">Sign Up</a>&nbsp;&nbsp;|&nbsp;&nbsp;
        <a href="<%=request.getContextPath()+"/update/request"%>" class="nav-button-<%=request.getParameter("page").equals("update") ? "on" : "off"%>">Update your Subscription</a>&nbsp;&nbsp;|&nbsp;&nbsp;
        <a href="<%=request.getContextPath()+"/unsubscribe/request"%>" class="nav-button-<%=request.getParameter("page").equals("unsubscribe") ? "on" : "off"%>">Unsubscribe</a>
        </div>
        <p></p>
