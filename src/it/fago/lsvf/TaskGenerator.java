package it.fago.lsvf;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * 
 * @author Stefano Fago
 * 
 */
public class TaskGenerator {

	private TaskGenerator() {
	}

	/**
	 * 
	 * @param verificationType
	 * @return
	 */
	public static final TaskGenerator getInstance(VerifyTaskType type) {
		switch (type) {
		case p7m:
			return new P7MTaskGenerator();
		case pdf:
			return new PdfTaskGenerator();
		default:
			throw new IllegalArgumentException(type.name());
		}
	}

	/**
	 * 
	 * @param f
	 * @return
	 */
	public Callable<VerifyTaskResult> generate(final File f) {
		throw new RuntimeException("Concrete Generator must be used!");
	}

	// =============================================================
	//
	//
	//
	// =============================================================

	private static class PdfTaskGenerator extends TaskGenerator {

		public Callable<VerifyTaskResult> generate(File f) {
			PDFTask t = new PDFTask();
			t.setFilepath(f.getAbsolutePath());
			return t;
		}
	}

	private static class P7MTaskGenerator extends TaskGenerator {
		public Callable<VerifyTaskResult> generate(File f) {
			P7MTask t = new P7MTask();
			t.setFilepath(f.getAbsolutePath());
			return t;
		}
	}

}// END
