package com.statics;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Pattern;

public class WordFilter {
  static String VIETNAMESE_DIACRITIC_CHARACTERS
          = "ẮẰẲẴẶĂẤẦẨẪẬÂÁÀÃẢẠĐẾỀỂỄỆÊÉÈẺẼẸÍÌỈĨỊỐỒỔỖỘÔỚỜỞỠỢƠÓÒÕỎỌỨỪỬỮỰƯÚÙỦŨỤÝỲỶỸỴ";

  static Pattern userNamePatternVN =
          Pattern.compile("(?:[" + VIETNAMESE_DIACRITIC_CHARACTERS + "]|[A-Z0-9])++",
                  Pattern.CANON_EQ | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
  //todo Thai Indo....

  static HashSet<String> singleBadWords = new HashSet<>();
  static ArrayList<String[]> comboBadWords = new ArrayList<>();

  public static void loadJson(String jsonFile) throws IOException {
    //todo adhoc first
    singleBadWords.add("donald");
    singleBadWords.add("đảng");
    comboBadWords.add(new String[] {"đảng", "cộng", "sản"});
  }

  public static boolean isValidUserName(String userName, String buildSource) {
    if(userName != null && !userName.isEmpty()) {
      String lowCase = userName.toLowerCase();
      boolean result = false;
      switch (buildSource) {
        case "VN":
          result = userNamePatternVN.matcher(lowCase).matches() && !singleBadWords.contains(lowCase) && checkComboBadWords(lowCase);
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
