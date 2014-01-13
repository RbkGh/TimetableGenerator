package edu.school.download;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import java.io.StringReader;

import javax.servlet.*;
import javax.servlet.http.*;

import oracle.xdo.template.FOProcessor;
import oracle.xdo.template.RTFProcessor;

public class DownloadServlet extends HttpServlet {
  private static final String CONTENT_TYPE_HTML = "text/html; charset=UTF-8";
  private static final String CONTENT_TYPE_RTF = "application/rtf; charset=UTF-8";
  private static final String CONTENT_TYPE_XML = "application/xml; charset=UTF-8";
  private static final String CONTENT_TYPE_PDF = "application/pdf; charset=UTF-8";
  private static final String CONTENT_TYPE_XLS = "application/xls; charset=UTF-8";
  private static final String CONTENT_TYPE_BINARY = "application/octet-stream; charset=UTF-8";

  public void init(ServletConfig config) throws ServletException {
    super.init(config);
  }

  /**Process the HTTP doGet request.
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String format = request.getParameter("format");
    HttpSession session = request.getSession(false);
    String xml = (String)session.getAttribute("scheduleXML");
    PrintWriter out = response.getWriter();
    if (xml == null) {
      response.setContentType(CONTENT_TYPE_HTML);
      out.println("<html>");
      out.println("<head><title>DownloadServlet</title></head>");
      out.println("<body>");
      out.println("<p>There was a problem converting the current schedule to XML.</p>");
      out.println("</body></html>");
    } else {
      if ("xml".equals(format)) {
        response.setContentType(CONTENT_TYPE_XML);
        response.setHeader( "Content-Disposition", "attachment; filename=\"timeSchedule." + format + "\"");
        response.setContentLength(xml.length());
        out.print(xml);
      } else {
        FOProcessor fop = new FOProcessor();
        try {
          InputStream templateIS = Thread.currentThread().getContextClassLoader().getResourceAsStream("/scheduleTemplate.rtf");
          RTFProcessor rtfp = new RTFProcessor(templateIS);
          ByteArrayOutputStream xslStream = new ByteArrayOutputStream();
          rtfp.setOutput(xslStream);
          rtfp.process();
          fop.setData(new StringReader(xml));
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          fop.setOutput(baos);
          fop.setTemplate(new ByteArrayInputStream(xslStream.toByteArray()));
          if ("rtf".equals(format)) {
            fop.setOutputFormat(FOProcessor.FORMAT_RTF);
            response.setContentType(CONTENT_TYPE_RTF);
          } else if ("html".equals(format)) {
            fop.setOutputFormat(FOProcessor.FORMAT_HTML);
            response.setContentType(CONTENT_TYPE_HTML);
          } else if ("xls".equals(format)) {
            fop.setOutputFormat(FOProcessor.FORMAT_EXCEL);
            response.setContentType(CONTENT_TYPE_XLS);
          } else if ("pdf".equals(format)) {
            fop.setOutputFormat(FOProcessor.FORMAT_PDF);
            response.setContentType(CONTENT_TYPE_PDF);
          } else {
            fop.setOutputFormat(FOProcessor.FORMAT_PDF);
            response.setContentType(CONTENT_TYPE_BINARY);
          }
          fop.generate();
          response.setHeader( "Content-Disposition", "attachment; filename=\"timeSchedule." + format + "\"");
//          response.setContentLength(baos.size());
          out.write(baos.toString());
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    out.close();
  }
}
