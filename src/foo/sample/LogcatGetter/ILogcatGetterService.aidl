package foo.sample.LogcatGetter;

interface ILogcatGetterService {
	 java.util.List<String> getDispData();
	 int saveLog(String FileName);
	 int stopWrite();
	 boolean isWritting();
	 String getWriteFilename();
	 void setLogBreak();
}
