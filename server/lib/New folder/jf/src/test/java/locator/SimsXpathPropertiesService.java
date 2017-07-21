package locator;

import java.util.Properties;

public interface SimsXpathPropertiesService {
	public Properties getProperties(boolean fromDB ,String taskName , String fileName);
}
