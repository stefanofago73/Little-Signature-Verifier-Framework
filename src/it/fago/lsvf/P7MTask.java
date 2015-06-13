package it.fago.lsvf;

import java.security.cert.CertStore;
import java.security.cert.CertStoreParameters;
import java.security.cert.CollectionCertStoreParameters;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.concurrent.Callable;

import org.bouncycastle.asn1.DERUTCTime;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerId;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Stefano Fago
 * 
 */
public class P7MTask implements Callable<VerifyTaskResult> {
	//
	private static final String cName = P7MTask.class.getName();
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
				bufferOut.append("\nprocessamento dati per file: ")
						.append(fullPathFile).append("\n");

				CMSSignedData s = new CMSSignedData(data);
				CertStore certStore = s.getCertificatesAndCRLs("Collection",
						"BC");
				bufferOut.append(
						"Certificate Default Type : "
								+ CertStore.getDefaultType()).append("\n\n");

				SignerInformationStore signerInfos = s.getSignerInfos();
				@SuppressWarnings("unchecked")
				Collection<SignerInformation> signers = (Collection<SignerInformation>) signerInfos
						.getSigners();

				bufferOut.append("Numero Signer : " + signers.size() + "\n\n");

				Iterator iter = signers.iterator();
				int level = 0;
				while (iter.hasNext()) {
					SignerInformation signer = (SignerInformation) iter.next();
					SignerId signerId = signer.getSID();
					SignerInfo signerInfo = signer.toSignerInfo();
					Enumeration objects = signer.getSignedAttributes()
							.get(CMSAttributes.signingTime).getAttrValues()
							.getObjects();
					bufferOut
							.append("\n###########################################################");
					bufferOut.append("\nFIRMA IN DATA: "
							+ ((DERUTCTime) (objects.nextElement())).getDate());
					bufferOut
							.append("\n###########################################################\n\n");
				}

				CertStoreParameters certStoreParaments = certStore
						.getCertStoreParameters();
				CollectionCertStoreParameters params = (CollectionCertStoreParameters) certStoreParaments;
				@SuppressWarnings("unchecked")
				Collection<X509CertificateObject> collection = (Collection<X509CertificateObject>) params
						.getCollection();
				for (Iterator<X509CertificateObject> iterC = collection
						.iterator(); iterC.hasNext();) {
					X509CertificateObject certObj = iterC.next();
					bufferOut
							.append("n###########################################################");
					bufferOut.append("\nCERTIFICATO: \n" + certObj);
					bufferOut
							.append("\n###########################################################\n");
				}

				final String tmp = bufferOut.toString();
				bufferOut.setLength(0);
				bufferOut = null;

				if (useConsole) {
					logger.info("\n\nVERIFY RESULT: \n {} ", tmp);
				}
				return VerifyTaskResult.createOKResult(tmp);
			} catch (Exception exc) {
				logger.error("Errore processing: [ {} ] ", fullPathFile, exc);
				return VerifyTaskResult.createKOResult(exc);
			} finally {
				logger = null;
			}
		}
		logger = null;
		return VerifyTaskResult.createKOResult(new IllegalArgumentException(
				"NO DATA!"));
	}

}// END