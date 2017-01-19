/**
 * 
 */
package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangle
 *
 */
public class APICodeGenenerator {

    private static final String API_SEPARATOR = "********************";
    private static final String API_NAME_HEAD = "===";
    private static final String API_PATH_HEAD = "%%%%";
    private static final String COMPLEX_PARAM_HEAD = "#####";

    /**
     * @param args
     */
    public static void main(String[] args) {
        String apiListFile = "/Users/zhangle/Documents/work/cooperation/Twzhy/api-list.txt";
        String templateFile = "/Users/zhangle/Documents/work/cooperation/Twzhy/api-template.txt";

        APICodeGenenerator gen = new APICodeGenenerator();
        gen.generateApiCode(apiListFile, templateFile);
    }

    public void generateApiCode(String apiListFile, String templateFile) {
        String template = readTemplate(templateFile);
        System.out.println("template:");
        System.out.println(template);
        List<ApiEntity> apiList = readApiList(apiListFile);
        for (ApiEntity api : apiList) {
            String apiStr = template.replace("${name}", api.getDesc());
            apiStr = apiStr.replace("${path}", api.getPath());
            apiStr = apiStr.replace("${method}", api.getMethodName());
            apiStr = apiStr.replace("${inputData}", api.getInputDataCode());
            System.out.println("//" + API_SEPARATOR);
            System.out.println(apiStr);
        }
    }

    public String readTemplate(String templateFile) {
        File f = new File(templateFile);
        FileInputStream fs = null;
        InputStreamReader in = null;
        BufferedReader br = null;
        StringBuffer sb = new StringBuffer();
        try {
            fs = new FileInputStream(f);
            in = new InputStreamReader(fs);
            br = new BufferedReader(in);
            String line = br.readLine();
            while (line != null) {
                sb.append(line + "\r\n");
                line = br.readLine();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception ignore) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ignore) {
                }
            }
            if (fs != null) {
                try {
                    fs.close();
                } catch (Exception ignore) {
                }
            }
        }
        return sb.toString();
    }

    private List<ApiEntity> readApiList(String apiListFile) {
        File f = new File(apiListFile);
        FileInputStream fs = null;
        InputStreamReader in = null;
        BufferedReader br = null;
        int doneCount = 0, failCount = 0, apiCount = 0;
        List<ApiEntity> apiList = new ArrayList<ApiEntity>();
        try {
            fs = new FileInputStream(f);
            in = new InputStreamReader(fs);
            br = new BufferedReader(in);
            String line = br.readLine();
            while (line != null) {
                if ("".equals(line)) {
                    line = br.readLine();
                    continue;
                }
                if (line.startsWith(API_NAME_HEAD)) {
                    apiCount++;
                    try {
                        ApiEntity api = readApiEntity(line, br);
                        apiList.add(api);
                        doneCount++;
                    } catch (Exception ex) {
                        System.out.println("Failed to read API " + line
                                + ", error:" + ex.getMessage());
                        failCount++;
                    }
                }
                line = br.readLine();
            }
            System.out.println("import file done." + apiCount
                    + " APIs scanned, " + doneCount + " APIs generated, "
                    + failCount + " APIs failed.");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception ignore) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ignore) {
                }
            }
            if (fs != null) {
                try {
                    fs.close();
                } catch (Exception ignore) {
                }
            }
        }
        return apiList;
    }

    private ApiEntity readApiEntity(String startLine, BufferedReader br)
            throws Exception {
        ApiEntity api = new ApiEntity();
        api.setDesc(startLine.substring(API_NAME_HEAD.length()).trim());
        String pathLine = br.readLine();
        if (!pathLine.startsWith(API_PATH_HEAD)) {
            throw new Exception("path line missing! (" + pathLine + ")");
        }
        api.setPath(pathLine.substring(API_PATH_HEAD.length()).trim());
        String line = br.readLine();
        IParamContainer paramContainer = api;
        while (!API_SEPARATOR.equals(line)) {
            if (line.startsWith(COMPLEX_PARAM_HEAD)) {
                paramContainer = findComplexParam(api, line);
                line = br.readLine();
                continue;
            }
            if ("".equals(line)) {
                line = br.readLine();
                continue;
            }
            String[] paramInfo = line.split("\\s+");
            if (paramInfo.length < 4 || paramInfo.length > 5) {
                throw new Exception("param info wrong! (" + line + ")");
            }
            ApiParam param = new ApiParam();
            param.setName(paramInfo[0].trim());
            param.setDesc(paramInfo[1].trim());
            param.setType(paramInfo[2].trim());
            if (paramInfo.length == 5) {
                param.setComment(paramInfo[4].trim());
            }
            paramContainer.addParam(param);
            line = br.readLine();
        }
        return api;
    }

    private IParamContainer findComplexParam(ApiEntity api, String line)
            throws Exception {
        String type = line.substring(COMPLEX_PARAM_HEAD.length());
        IParamContainer result = null;
        boolean isArray = false;
        if (type.contains("[]")) {
            isArray = true;
            type = type.replace("[]", "");
        }
        for (ApiParam param : api.getParamList()) {
            if (param.getType().contains(type) && param.isArray == isArray) {
                result = param;
            }
        }
        if (result == null) {
            throw new Exception("Cannot find complex param of type '" + type
                    + "' in line:" + line);
        }
        return result;
    }

    class ApiParam implements IParamContainer {
        String name;
        String desc;
        String type;
        String comment;
        List<ApiParam> params;
        boolean isComplex;
        boolean isArray;

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name
         *            the name to set
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return the desc
         */
        public String getDesc() {
            return desc;
        }

        /**
         * @param desc
         *            the desc to set
         */
        public void setDesc(String desc) {
            this.desc = desc;
        }

        /**
         * @return the type
         */
        public String getType() {
            return type;
        }

        /**
         * @param type
         *            the type to set
         */
        public void setType(String type) {
            this.type = type;
            if (type.contains("List")) {
                isArray = true;
            } else {
                isArray = false;
            }
            String lowerType = type.toLowerCase();
            if ("string".equals(lowerType) || "int".equals(lowerType)
                    || "integer".equals(lowerType) || "long".equals(lowerType)
                    || "short".equals(lowerType) || "byte".equals(lowerType)
                    || "char".equals(lowerType) || "double".equals(lowerType)
                    || "float".equals(lowerType)) {
                isComplex = false;
            } else {
                isComplex = true;
            }
        }

        /**
         * @return the comment
         */
        public String getComment() {
            return comment;
        }

        /**
         * @param comment
         *            the comment to set
         */
        public void setComment(String comment) {
            this.comment = comment;
        }

        /**
         * @return the params
         */
        public List<ApiParam> getParams() {
            return params;
        }

        /**
         * @return the isComplex
         */
        public boolean isComplex() {
            return isComplex;
        }

        /**
         * @return the isArray
         */
        public boolean isArray() {
            return isArray;
        }

        @Override
        public void addParam(ApiParam param) throws Exception {
            if (!isComplex) {
                throw new Exception(name + "(type:" + type
                        + ") is not complex param.");
            }
            if (params == null) {
                params = new ArrayList<ApiParam>();
            }
            params.add(param);
        }

        public String genCode(String parentJsonName, int level) {
            String Type = type.substring(0, 1).toUpperCase()
                    + type.substring(1).toLowerCase();
            String prefix = ""; // prefix whitespaces
            for (int i = 0; i < level; i++) {
                prefix += "    ";
            }
            String paramComment = "// " + (desc == null ? "" : desc) + ", "
                    + (comment == null ? "" : comment);
            if (isComplex) {
                StringBuffer sb = new StringBuffer();
                if (isArray) {
                    sb.append(prefix + paramComment + "\r\n");
                    sb.append(prefix + "JSONArray " + name
                            + " = " + parentJsonName + ".optJSONArray(\"" + name + "\");\r\n");
                    sb.append(prefix + "for (int i = 0; i < " + name
                            + ".size(); i++) {\r\n");
                    sb.append(prefix
                            + "    JSONObject obj = " + name + ".getJSONObject(i);\r\n");
                    for (ApiParam param : params) {
                        sb.append(param.genCode("obj", level + 1));
                    }
                    sb.append(prefix + "}\r\n");
                } else {
                    sb.append(prefix + paramComment + "\r\n");
                    sb.append(prefix + "JSONObject " + name
                            + " = input.optJSONObject(\"" + name + "\");\r\n");
                    sb.append(prefix + "{\r\n");
                    for (ApiParam param : params) {
                        sb.append(param.genCode(name, level + 1));
                    }
                    sb.append(prefix + "}\r\n");
                }
                return sb.toString();
            }
            return prefix + type + " " + name + " = " + parentJsonName + ".opt"
                    + Type + "(\"" + name + "\"); " + paramComment + "\r\n";
        }
    }

    class ApiEntity implements IParamContainer {
        String desc;
        String path;
        String methodName;
        List<ApiParam> paramList;

        /**
         * @return the desc
         */
        public String getDesc() {
            return desc;
        }

        /**
         * @param desc
         *            the desc to set
         */
        public void setDesc(String desc) {
            this.desc = desc;
        }

        /**
         * @return the path
         */
        public String getPath() {
            return path;
        }

        /**
         * @param path
         *            the path to set
         */
        public void setPath(String path) {
            this.path = path;
            methodName = "";
            if (path != null && !"".equals(path)) {
                String[] pathArr = path.split("/");
                for (int i = 0; i < pathArr.length; i++) {
                    if (pathArr[i] == null || "".equals(pathArr[i])) {
                        continue;
                    }
                    if ("".equals(methodName)) {
                        methodName += pathArr[i];
                    } else {
                        methodName += pathArr[i].substring(0, 1).toUpperCase()
                                + pathArr[i].substring(1);
                    }
                }
            }
        }

        /**
         * @return the methodName
         */
        public String getMethodName() {
            return methodName;
        }

        /**
         * @return the code of inputData
         */
        public String getInputDataCode() {
            StringBuffer sb = new StringBuffer();
            if (paramList == null) {
                return "/**no params**/";
            }
            for (ApiParam param : paramList) {
                sb.append(param.genCode("input", 3));
                sb.append("\r\n");
            }
            return sb.toString();
        }

        /**
         * @return the paramList
         */
        public List<ApiParam> getParamList() {
            return paramList;
        }

        /**
         * @param paramList
         *            the paramList to set
         */
        @Override
        public void addParam(ApiParam param) throws Exception {
            if (paramList == null) {
                paramList = new ArrayList<ApiParam>();
            }
            paramList.add(param);
        }
    }

    public interface IParamContainer {
        void addParam(ApiParam param) throws Exception;
    }
}
