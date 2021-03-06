package loon.utils.debugging;

import loon.utils.FileUtils;


final public class Log {

	public static void exception(Object o) {
		if (o instanceof Throwable) {
			android.util.Log.e("exception", ((Throwable) o).getMessage());
		}
	}

	public static void debugWrite(String text) {
	}

	private static final int MAX_LOG_MESSAGES = 25;

	private static LogMessage[] store;

	private static int oldestMessageIndex;

	private static int newestMessageIndex;

	private Level level = Level.INFO;

	private LogFormat logFormat;

	private String app;

	static {
		clear();
	}

	Log(Class<?> clazz) {
		this(FileUtils.getExtension(clazz.getName()), 0);
	}

	Log(String app, int type) {
		this.logFormat = new LogFormat(true, type);
		this.app = app;
		this.level = Level.ALL;
	}

	public Level getLogLevel() {
		return level;
	}

	
	public void setLevel(int level) {
		if (level == Level.DEBUG.level) {
			this.level = Level.DEBUG;
		} else if (level == Level.INFO.level) {
			this.level = Level.INFO;
		} else if (level == Level.WARN.level) {
			this.level = Level.WARN;
		} else if (level == Level.ERROR.level) {
			this.level = Level.ERROR;
		} else if (level == Level.IGNORE.level) {
			this.level = Level.IGNORE;
		} else if (level == Level.ALL.level) {
			this.level = Level.ALL;
		} else {
			throw new IllegalArgumentException("Levels of error messages !");
		}
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public boolean isVisible() {
		return logFormat.isShow();
	}

	public void setVisible(boolean show) {
		logFormat.setShow(show);
	}

	public void hide() {
		setVisible(false);
	}

	public void show() {
		setVisible(true);
	}

	public void d(String message) {
		if (level.level == Level.ALL.level || level.level <= Level.DEBUG.level) {
			addLogMessage(message, Level.DEBUG, null);
		}
	}

	public void d(String message, Throwable tw) {
		if (level.level == Level.ALL.level || level.level <= Level.DEBUG.level) {
			addLogMessage(message, Level.DEBUG, tw);
		}
	}

	public void i(String message) {
		if (level.level == Level.ALL.level || level.level <= Level.INFO.level) {
			addLogMessage(message, Level.INFO, null);
		}
	}

	public void i(String message, Throwable tw) {
		if (level.level <= Level.INFO.level) {
			addLogMessage(message, Level.INFO, tw);
		}
	}

	public void w(String message) {
		if (level.level == Level.ALL.level || level.level <= Level.WARN.level) {
			addLogMessage(message, Level.WARN, null);
		}
	}

	public void w(String message, Throwable tw) {
		if (level.level == Level.ALL.level || level.level <= Level.WARN.level) {
			addLogMessage(message, Level.WARN, tw);
		}
	}

	public void e(String message) {
		if (level.level == Level.ALL.level || level.level <= Level.ERROR.level) {
			addLogMessage(message, Level.ERROR, null);
		}
	}

	public void e(String message, Throwable tw) {
		if (level.level <= Level.ERROR.level) {
			addLogMessage(message, Level.ERROR, tw);
		}
	}

	public LogFormat getLogFormat() {
		return logFormat;
	}

	public boolean isDebugEnabled() {
		return level.level <= Level.DEBUG.level;
	}

	public boolean isInfoEnabled() {
		return level.level <= Level.INFO.level;
	}

	
	private synchronized void addLogMessage(String message, Level level,
			Throwable throwable) {
		if (message == null) {
			message = "";
		}
		String text = message;
		if (throwable != null) {
			text += " " + throwable.toString();
		}
		newestMessageIndex = (newestMessageIndex + 1) % MAX_LOG_MESSAGES;
		if (newestMessageIndex == oldestMessageIndex) {
			store[newestMessageIndex].setLogMessage(level, text);
			oldestMessageIndex = (oldestMessageIndex + 1) % MAX_LOG_MESSAGES;
		} else {
			store[newestMessageIndex] = new LogMessage(level, text);
			if (oldestMessageIndex < 0) {
				oldestMessageIndex = 0;
			}
		}
		LogMessage log = store[newestMessageIndex];
		logFormat.out(log.time, app, log.level.levelString, log.message);
	}

	public synchronized LogMessage[] getLogMessages() {
		int numberOfMessages;
		if (newestMessageIndex < 0) {
			numberOfMessages = 0;
		} else if (newestMessageIndex >= oldestMessageIndex) {
			numberOfMessages = newestMessageIndex - oldestMessageIndex + 1;
		} else {
			numberOfMessages = MAX_LOG_MESSAGES;
		}
		LogMessage[] copy = new LogMessage[numberOfMessages];
		for (int i = 0; i < numberOfMessages; i++) {
			int index = newestMessageIndex - i;
			if (index < 0) {
				index = MAX_LOG_MESSAGES + index;
			}
			copy[numberOfMessages - i - 1] = store[index];
		}
		return copy;
	}

	public static synchronized void clear() {
		oldestMessageIndex = -1;
		newestMessageIndex = -1;
		store = new LogMessage[MAX_LOG_MESSAGES];
	}

}
