package it.fago.lsvf;

/**
 * 
 * @author Stefano Fago
 * 
 */
public class VerifyTaskResult {
	//
	private String result;
	//
	private Throwable error;

	/**
	 * 
	 * @return
	 */
	public String getResult() {
		return result;
	}

	/**
	 * 
	 * @param result
	 */
	public void setResult(String result) {
		this.result = result;
	}

	/**
	 * 
	 * @return
	 */
	public Throwable getError() {
		return error;
	}

	/**
	 * 
	 * @param error
	 */
	public void setError(Throwable error) {
		this.error = error;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isError() {
		return error != null;
	}

	/**
	 * 
	 * @param result
	 * @return
	 */
	public final static VerifyTaskResult createOKResult(final String result) {
		final VerifyTaskResult res = new VerifyTaskResult();
		res.setError(null);
		res.setResult(result);
		return res;
	}

	/**
	 * 
	 * @param error
	 * @return
	 */
	public final static VerifyTaskResult createKOResult(final Throwable error) {
		final VerifyTaskResult res = new VerifyTaskResult();
		res.setError(error);
		res.setResult(null);
		return res;
	}

}// END
