package pureplus.json;

import java.util.List;

public class JSONWriter {

    static String toJSONElement(Object obj) {
        if (obj instanceof String) {
            String  str = (String)obj;
            return "\"" + str + "\"";
        } else if (obj instanceof Integer) {
            return Integer.toString((int)obj);
        } else if (obj instanceof Double) {
            return Double.toString((double)obj);
        } else if (obj instanceof Boolean) {
            boolean  b = (boolean)obj;
            return b?"true":"false";
        } else if (obj instanceof JSONObject) {
            return toJSON((JSONObject)obj);
        } else if (obj instanceof JSONArray) {
            return toJSON((JSONArray)obj);
        } else {
            return null;
        }
    }

    public static String toJSON(JSONArray ary) {
        StringBuilder  sb = new StringBuilder();

        sb.append("[");
        int  n = ary.size();

        sb.append(toJSONElement(ary.get(0)));

        for (int i=1; i<n; i++) {
            sb.append(',');
            sb.append(toJSONElement(ary.get(i)));
        }

        sb.append("]");

        return sb.toString();
    }

    static String toJSON(JSONMember memb) {
        StringBuilder  sb = new StringBuilder();

        sb.append("\"");
        sb.append(memb.getKey());
        sb.append("\" : ");
        sb.append(toJSONElement(memb.getValue()));

        return sb.toString();
    }

    public static String toJSON(JSONObject obj) {
        StringBuilder  sb = new StringBuilder();

        sb.append("{");
       
        List<JSONMember>  memb = obj.members;
        int  n = memb.size();

        sb.append(toJSON(memb.get(0)));

        for (int i=1; i<n; i++) {
            sb.append(',');
            sb.append(toJSON(memb.get(i)));
        }

        sb.append("}");

        return sb.toString();
    }
}
