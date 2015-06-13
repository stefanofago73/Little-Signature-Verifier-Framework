package it.fago.lsvf;

import static it.fago.lsvf.SignaturesVerifierUtils.checkIfArgumentsArePresent;
import static it.fago.lsvf.SignaturesVerifierUtils.loadProviderAndStore;
import static it.fago.lsvf.SignaturesVerifierUtils.numOfWorker;
import static it.fago.lsvf.SignaturesVerifierUtils.verifyFileOrDirectory;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author U824851
 * 
 */
public class SignaturesVerifier {
	//
	private static Logger logger;
	//
	private CompletionService<VerifyTaskResult> workers;
	//
	private ExecutorService pool;

	/**
	 * 
	 * @param numOfWorker
	 */
	public void init(int numOfWorker) {
		logger = LoggerFactory.getLogger(SignaturesVerifier.class);
		logger.info("###############################################\nSYSTEM STARTUP!\n###############################################\n");
		pool = Executors.newFixedThreadPool(numOfWorker);
		workers = new ExecutorCompletionService<VerifyTaskResult>(pool);
		PdfUtils.init();
	}

	/**
	 * 
	 */
	public void destroy() {

		Object endElement = null;
		int counter = 0;
		try {
			while ((endElement = workers.poll(3000L, TimeUnit.MILLISECONDS)) != null) {
				counter++;
			}
		} catch (InterruptedException e) {
			logger.warn("{} interrupted while waiting to complete all task!",
					Thread.currentThread().getName());
		}
		pool.shutdownNow();
		try {
			pool.awaitTermination(5000L, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			logger.warn("{} interrupted while waiting to shutdown pool!",
					Thread.currentThread().getName());
		}
		pool = null;
		logger.info("\nExecuted : {} TASK\n", counter);
		logger.info("###############################################\nSYSTEM SHUTDONW!\n###############################################\n");
		PdfUtils.destroy();
		logger = null;
		System.exit(0);
	}

	/**
	 * 
	 * @param verificationType
	 * @param targets
	 */
	public void service(VerifyTaskType type, File[] targets) {

		logger.info("It will be read {} file!...",
				(targets != null ? targets.length : 0));

		TaskGenerator generator = TaskGenerator.getInstance(type);
		for (int i = 0; i < targets.length; i++) {
			Callable<VerifyTaskResult> task = generator.generate(targets[i]);
			workers.submit(task);
		}
	}

	// ======================================================================
	//
	//
	//
	// ======================================================================

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		checkIfArgumentsArePresent(args);
		loadProviderAndStore();
		final String vType = args[0];
		File[] targetFiles = verifyFileOrDirectory(args[1], args[0]);
		int numOfWorker = numOfWorker(args.length < 3 ? null : args[2]);
		SignaturesVerifier verifier = new SignaturesVerifier();
		verifier.init(numOfWorker);
		verifier.service(VerifyTaskType.valueOf(vType), targetFiles);
		verifier.destroy();
	}

}// END