package it.fago.lsvf;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PDFTask implements Callable<VerifyTaskResult> {
	//
	private static final String cName = PDFTask.class.getName();
	//
	private String fullPathFile;
	//
	private boolean useConsole = true;

	public void setFilepath(String fullPathFile) {
		this.fullPathFile = fullPathFile;
	}

	public void useConsoleOutput() {
		useConsole = true;
	}

	@Override
	public VerifyTaskResult call() throws Exception {

		Logger logger = LoggerFactory.getLogger(cName);

		byte[] data = SignaturesVerifierUtils.loadContentFromFile(fullPathFile);

		if (data != null) {
			StringBuilder bufferOut = new StringBuilder();
			try {
				PdfUtils.analizePdf(fullPathFile, data, bufferOut);
			} catch (Exception e) {
				logger.error("problem in: {} --> {} ",this,e.toString());
				logger = null;
				return VerifyTaskResult.createKOResult(e);
			}
			
			final String tmp = bufferOut.toString();
			bufferOut.setLength(0);
			bufferOut = null;

			if (useConsole) {
				logger.info("\n\nVERIFY RESULT: \n {} ", tmp);
			}
			logger = null;
			return VerifyTaskResult.createOKResult(tmp);
		}
		logger = null;
		return VerifyTaskResult.createKOResult(new IllegalArgumentException(
				"NO DATA!"));
	}

}// END