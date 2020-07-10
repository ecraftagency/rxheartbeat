package tools;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CSV2Json {
  public static void main(String[] args) throws IOException {
    Files.list(Paths.get("data/csv")).forEach(e -> {
      try {
        File input = e.toFile();
        String outputPath = "data/json/" + e.getFileName().toString().replace("csv", "json");
        File output = new File(outputPath);
        if (output.exists()) {
          if (output.delete() && output.createNewFile())
            csv2json(input, output);
          else
            System.out.println("failed to create " + outputPath);
        }
        else {
          if (output.createNewFile())
            csv2json(input, output);
          else
            System.out.println("failed to create " + outputPath);
        }
      }
      catch (Exception ioe) {
        ioe.printStackTrace();
      }
    });
  }

  public static void csv2json(File input, File output) throws IOException {
    System.out.println("convert " + input.getName());
    List<Map<?, ?>> data = readObjectsFromCsv(input);
    writeAsJson(data, output);
  }

  public static List<Map<?, ?>> readObjectsFromCsv(File file) throws IOException {
    CsvSchema bootstrap = CsvSchema.emptySchema().withHeader();
    CsvMapper csvMapper = new CsvMapper();
    MappingIterator<Map<?, ?>> mappingIterator = csvMapper.reader(Map.class).with(bootstrap).readValues(file);
    return mappingIterator.readAll();
  }

  public static void writeAsJson(List<Map<?, ?>> data, File file) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(file, data);
  }
}
