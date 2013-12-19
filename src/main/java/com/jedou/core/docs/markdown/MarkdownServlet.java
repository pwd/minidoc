package com.jedou.core.docs.markdown;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MarkdownServlet extends HttpServlet {
	private static final long serialVersionUID = -2294431231475800457L;
	private String header;
	private String footer;
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String path = req.getRequestURI();
		String root = getServletContext().getRealPath("/");
		StringBuffer sb = new StringBuffer();
		sb.append(root).append(path);
		if (fileExists(sb.toString())) {
			Reader markdownReader = new FileReader(sb.toString());
			try {
				String content = Markdown.transformMarkdown(markdownReader);
				String[] ps = path.split("/");
				String title = ps[ps.length-1];
				if (header == null) loadTemplateFile(root+"/header.html", title);
				if (footer == null) loadTemplateFile(root+"/footer.html");
				resp.setContentType("text/html; charset=utf-8");
				resp.getWriter().println(header);
				resp.getWriter().println(content);
				resp.getWriter().println(footer);
			} catch (ParseException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		else
			resp.sendError(HttpServletResponse.SC_NOT_FOUND, path);
	}

	private boolean fileExists(String path) {
		return new File(path).exists();
	}
	
	private void loadTemplateFile(String fileName, String...args) throws IOException {
		FileReader fr = new FileReader(fileName);
		StringBuffer sb = new StringBuffer();
		int c = -1;
		while((c = fr.read()) != -1) {
			sb.append((char)c);
		}
		if (fileName.lastIndexOf("header.html") > -1) {
			if (args != null)
				this.header = sb.toString().replaceFirst("__title__", args[0]);
			else
				this.header = sb.toString();
		}
		else
			this.footer = sb.toString();
	}
	
}
