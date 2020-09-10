package com.statics;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Pattern;

public class WordFilter {
  static String VIETNAMESE_DIACRITIC_CHARACTERS
          = "ẮẰẲẴẶĂẤẦẨẪẬÂÁÀÃẢẠĐẾỀỂỄỆÊÉÈẺẼẸÍÌỈĨỊỐỒỔỖỘÔỚỜỞỠỢƠÓÒÕỎỌỨỪỬỮỰƯÚÙỦŨỤÝỲỶỸỴắằẳẵặăấầẩẫậâáàãảạđếềểễệêéèẻẽẹíìỉĩịốồổỗộôớờởỡợơóòõỏọứừửữựưúùủũụýỳỷỹỵ";

  static Pattern userNamePatternVN =
          Pattern.compile("(?:[" + VIETNAMESE_DIACRITIC_CHARACTERS + "]|[A-Z0-9])++",
                  Pattern.CANON_EQ | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

  static HashSet<String>      singleBadWords  = new HashSet<>();
  static ArrayList<String[]>  comboBadWords   = new ArrayList<>();

  public static void loadText(String filterFile) throws IOException {
    BufferedReader reader;
      reader = new BufferedReader(new FileReader(filterFile));
      String line = reader.readLine();
      while (line != null) {
        singleBadWords.add(line.trim().toLowerCase());
        line = reader.readLine();
      }
  }

  public static boolean isValidInput(String input, String buildSource) {
    if(input != null && !input.isEmpty()) {
      String lowCase = input.toLowerCase();
      boolean result = false;
      switch (buildSource) {
        case "VN":
          result = /*userNamePatternVN.matcher(lowCase).matches() && */!singleBadWords.contains(lowCase) && checkComboBadWords(lowCase);
          break;
        case "TH":
          //todo
          break;
        case "ID":
          //todo
          break;
        default:
      }
      if (result) {
        for (String word : singleBadWords) {
          if (lowCase.contains(word)) {
            return false;
          }
        }
        return true;
      }
    }
    return false;
  }

  public static boolean checkComboBadWords(String userName) {
    for(String[] cbWords : comboBadWords) {
      int count = 0;
      for(String word : cbWords) {
        if(!userName.contains(word))
          break;
        count++;
      }
      if(count == cbWords.length)
        return false;
    }
    return true;
  }
}
