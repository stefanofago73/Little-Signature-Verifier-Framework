## Little Signatures Verifier Framework ##


### Description ###

This is a simple and really raw project that offer a complete framework<br/>
to verify signatures of PDF or P7M Documents<br/> 
<br/>
<br/>
WARNING:<br/>
<br/>
<br/>
It work well (and it really used) but there are more things to fix( other than programming style ;-P)<br/>
<br/>
- there are some unused but useful objects<br/>
<br/>
- there are some timezone/date elements to add<br/>
<br/>
- a better tasks management<br/>
<br/>
...but it was made in so little time...be patient!<br/>
<br/>
<br/>
<br/>
<br/>
To use it as command line utility:<br/>
<pre><code>

java -cp ... it.fago.lsvf.SignaturesVerifier [pdf or p7m] [folder or file]

</code></pre>
<br/>
You can customize it and improve it...<br/>
<br/>
As command line utility, it don't exposed the result from task but<br/>
they are available by code...<br/>
<br/>
<pre><code>

		SignaturesVerifier verifier = new SignaturesVerifier();
		verifier.init(numOfWorker);
		verifier.service(VerifyTaskType.valueOf(vType), targetFiles);
		verifier.destroy();

</code></pre>
<br/>
The service method can be changed to use the VerifyTaskResult <br/>
from internal task, submitted to workers pool...<br/>
<br/>
<pre><code>

		public void service(VerifyTaskType type, File[] targets) {

				TaskGenerator generator = TaskGenerator.getInstance(type);
				for (int i = 0; i < targets.length; i++) {
					Callable<VerifyTaskResult> task = generator.generate(targets[i]);
					Future<VerifyTaskResult> future = workers.submit(task);
					...
				}
		}

</code></pre>
<br/>
<br/>
<br/>
More things You can do with it! Happy Coding! ;-P<br/>
<br/>
<br/>