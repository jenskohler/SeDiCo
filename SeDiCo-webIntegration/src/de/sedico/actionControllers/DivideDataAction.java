package de.sedico.actionControllers;

import java.io.BufferedInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

@ManagedBean(name = "divideDataAction")
@SessionScoped

/**
 * Diese Klasse teilt die Daten
 * @author jens
 *
 */
public class DivideDataAction {
	private static Logger log = Logger.getLogger(DivideDataAction.class);
	/**
	 * Diese Methode führt durch die Applikation.
	 * @return /protected/finish.xhtml
	 */
	public String forwardToEnd() {
		log.info("Entering forwardToEnd");
		FacesContext fc = FacesContext.getCurrentInstance();

		try {
			// OutputStream out = null;

			HttpServletRequest request = (HttpServletRequest) fc
					.getExternalContext().getRequest();
			String username = request.getUserPrincipal().getName();

			HttpServletResponse response = (HttpServletResponse) fc
					.getExternalContext().getResponse();
			ServletContext ctx = (ServletContext) FacesContext
					.getCurrentInstance().getExternalContext().getContext();

			String rootPath = ctx.getRealPath("/");

			String zipFile = rootPath + "WEB-INF/zipPackage/"
					+ request.getUserPrincipal().getName() + ".zip";
			String srcDir = rootPath + "WEB-INF/zipPackage/"
					+ request.getUserPrincipal().getName() + "/";

			// create byte buffer
			byte[] buffer = new byte[1024];

			FileOutputStream fos = new FileOutputStream(zipFile);
			ZipOutputStream zos = new ZipOutputStream(fos);

			String docuSource = rootPath + "WEB-INF/zipPackage/SeDiCo-Docu.pdf";

			// copy SeDiCo-Docu.pdf to user folder so that it is zipped together
			// with the other user files
			InputStream in = new FileInputStream(docuSource);

			OutputStream out = new FileOutputStream(srcDir + "SeDiCo-Docu.pdf");

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			log.info("File copied into user folder.");

			// end copying SeDiCo-Docu

			// copy SeDiCo-partition.zip to user folder, so that the user can
			// download the project together with the other user files
			String partitionSource = rootPath
					+ "WEB-INF/zipPackage/SeDiCo-partition.zip";

			// copy SeDiCo-Docu.pdf to user folder so that it is zipped together
			// with the other user files
			in = new FileInputStream(partitionSource);

			out = new FileOutputStream(srcDir + "SeDiCo-partition.zip");

			buf = new byte[1024];
			len = 0;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			log.info("File copied into user folder.");

			// end copying SeDiCo-partition.zip

			// copy SeDiCo-partition-demo.zip to user folder, so that the user
			// can download the project together with the other user files
			String partitionDemoSource = rootPath
					+ "WEB-INF/zipPackage/SeDiCo-partition-demo.zip";

			// copy SeDiCo-Docu.pdf to user folder so that it is zipped together
			// with the other user files
			in = new FileInputStream(partitionDemoSource);

			out = new FileOutputStream(srcDir + "SeDiCo-partition-demo.zip");

			buf = new byte[1024];
			len = 0;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			log.info("File copied into user folder.");
			
			
			// end copying SeDiCo-partition-demo.zip

			File dir = new File(srcDir);
			File[] files = dir.listFiles();

			for (int i = 0; i < files.length; i++) {
				log.info("Adding file: " + files[i].getName());

				FileInputStream fis = new FileInputStream(files[i]);

				// begin writing a new ZIP entry, positions the stream to the
				// start of the entry data
				zos.putNextEntry(new ZipEntry(files[i].getName()));

				int length;

				while ((length = fis.read(buffer)) > 0) {
					zos.write(buffer, 0, length);
				}

				zos.closeEntry();

				// close the InputStream
				fis.close();
			}

			// close the ZipOutputStream
			zos.close();

		} catch (IOException e) {
			// to be implemented
		}

		return "/protected/finish.xhtml";
	}
/**
 * Diese Methode ermöglicht den ZIP-file-Download.
 * @return null;
 */
	public String downloadZipFile() {
		log.info("Entering downloadZipFile");

		FacesContext fc = FacesContext.getCurrentInstance();

		try {

			HttpServletRequest request = (HttpServletRequest) fc
					.getExternalContext().getRequest();
			String username = request.getUserPrincipal().getName();

			HttpServletResponse response = (HttpServletResponse) fc
					.getExternalContext().getResponse();
			ServletContext ctx = (ServletContext) FacesContext
					.getCurrentInstance().getExternalContext().getContext();

			String rootPath = ctx.getRealPath("/");
			String zipFile = rootPath + "WEB-INF/zipPackage/"
					+ request.getUserPrincipal().getName() + ".zip";

			File zip = new File(zipFile);

			response.setContentType("application/octet-stream");
			response.addHeader("Content-Disposition", "attachment; filename=\""
					+ username + ".zip\"");
			long length = zip.length();
			byte[] buf = new byte[1024];
			BufferedInputStream in = new BufferedInputStream(
					new FileInputStream(zipFile));
			ServletOutputStream sout = response.getOutputStream();
			response.setContentLength((int) length);

			while ((in != null) && ((length = in.read(buf)) != -1)) {
				sout.write(buf, 0, (int) length);
			}

			sout.flush();

			if (sout != null) {
				sout.close();
			}
			if (in != null) {
				in.close();
			}

			FacesContext.getCurrentInstance().responseComplete();

		} catch (IOException e) {
			// to be implemented
		}

		return null;
	}

}
