package task5.shop.initialization;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Reader {

	public String readFile(String fileName) throws URISyntaxException {
		Path path = Paths.get(this.getClass().getClassLoader().getResource(fileName).toURI());
		try (Stream<String> lines = Files.lines(path)) {
			return lines.collect(Collectors.joining());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
