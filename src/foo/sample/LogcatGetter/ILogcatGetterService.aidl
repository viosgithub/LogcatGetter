package foo.sample.LogcatGetter;

interface ILogcatGetterService {
	 int saveLog(String FileName);
	 int stopWrite();
	 boolean isWritting();
	 String getWriteFilename();
	 void setLogBreak();
}
