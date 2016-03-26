import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Main {
    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Test t = new Test();
        getConstructors(t);
        geMethod(t);
        getFiled(t);
        loadshowstrs();
        setValue();
    }

    /**
     * ��ȡ���캯��
     * 
     * @param test
     */
    private static void getConstructors(Test test) {
        Class c = test.getClass();
        String classname = c.getName();
        try {
            Constructor[] theConstructors = c.getDeclaredConstructors();
            for (int i = 0; i < theConstructors.length; i++) {
                // ���������ͷ�������
                int mod = theConstructors[i].getModifiers();
                System.out
                        .print(Modifier.toString(mod) + " " + classname + "(");
                // ��ȡָ�����췽���Ĳ����ļ���
                Class[] parameterTypes = theConstructors[i].getParameterTypes();
                for (int j = 0; j < parameterTypes.length; j++) {
                    // �����ӡ�����б�
                    System.out.print(parameterTypes[j].getName());
                    if (parameterTypes.length > j + 1) {
                        System.out.print(", ");
                    }
                }
                System.out.println(")");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * ��ȡ������ķ���
     */
    public static void geMethod(Test test) {

        Class c = test.getClass();
        String classname = c.getName();

        /*
         * Note: ����getDeclaredMethods()ֻ�ܻ�ȡ���ɵ�ǰ�ඨ������з��������ܻ�ȡ�Ӹ���̳еķ���
         * ����getMethods() �����ܻ�ȡ����ǰ�ඨ���public������Ҳ�ܵõ��Ӹ���̳к��Ѿ�ʵ�ֽӿڵ�public����
         * ����Ŀ����ĵ�����������������ϸ������
         */
        Method[] methods = c.getDeclaredMethods();
        // Method[] methods = c.getMethods();

        for (int i = 0; i < methods.length; i++) {

            // ��ӡ���������������
            int mod = methods[i].getModifiers();
            System.out.print(Modifier.toString(mod) + " ");

            // ��������ķ�������
            System.out.print(methods[i].getReturnType().getName());

            // ��ȡ����ķ�����
            System.out.print(" " + methods[i].getName() + "(");

            // ��ӡ��������Ĳ����б�
            Class[] parameterTypes = methods[i].getParameterTypes();
            for (int j = 0; j < parameterTypes.length; j++) {
                System.out.print(parameterTypes[j].getName());
                if (parameterTypes.length > j + 1) {
                    System.out.print(", ");
                }
            }
            System.out.println(")");
        }
    }

    /**
     * ��ȡ��Ա����
     * 
     * @param test
     */
    public static void getFiled(Test test) {
        Class c = test.getClass(); // ��ȡClass��Ķ���ķ���֮һ

        try {
            System.out.println("public & ��public ����");
            Field[] fa = c.getDeclaredFields();
            for (int i = 0; i < fa.length; i++) {

                Class cl = fa[i].getType(); // ���Ե�����

                int md = fa[i].getModifiers(); // ���Ե�������

                Field f = c.getDeclaredField(fa[i].getName()); // ���Ե�ֵ
                f.setAccessible(true); // Very Important
                Object value = (Object) f.get(new Test());

                if (value == null) {
                    System.out.println(Modifier.toString(md) + " " + cl + " : "
                            + fa[i].getName());
                } else {
                    System.out.println(Modifier.toString(md) + " " + cl + " : "
                            + fa[i].getName() + " = " + value.toString());
                }
            }

            System.out.println("public ����");
            Field[] fb = c.getFields();
            for (int i = 0; i < fb.length; i++) {

                Class cl = fb[i].getType(); // ���Ե�����

                int md = fb[i].getModifiers(); // ���Ե�������

                Field f = c.getField(fb[i].getName()); // ���Ե�ֵ
                f.setAccessible(true);
                Object value = (Object) f.get(new Test());

                // �ж������Ƿ񱻳�ʼ��
                if (value == null) {
                    System.out.println(Modifier.toString(md) + " " + cl + " : "
                            + fb[i].getName());
                } else {
                    System.out.println(Modifier.toString(md) + " " + cl + " : "
                            + fb[i].getName() + " = " + value.toString());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ���޷�����ʱ����������ʼ�����÷��䡣
     */
    public static void loadshowstr() {
        try {
            Class c = Class.forName("Test");
            Constructor ct = c.getConstructor(null);
            Object obj = ct.newInstance(null);

            Class paramTypes[] = getParamTypes(c, "showstr");
            Method meth = c.getDeclaredMethod("showstr", paramTypes);
            meth.setAccessible(true);
            String[] str = new String[1];
            str[0] = "str";
            Object[] params = str;
            String s = (String) meth.invoke(obj, params);
            System.out.println("load " + s);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * ֱ�Ӵ�������Ȼ�����÷���
     */
    public static void loadshowstrs() {
        try {
            Test t = new Test();
            Method meth = t.getClass().getDeclaredMethod("showstr",
                    String.class, String.class);
            meth.setAccessible(true);
            String s = (String) meth.invoke(t, "str1", "str2");
            System.out.println("load " + s);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Field ��get set ����
     */
    public static void setValue() {
        try {
            Class c = Class.forName("Test");
            Constructor ct = c.getConstructor(int.class, String.class,
                    String.class);
            Object obj = ct.newInstance(0, "a", "b");

            Field f = c.getField("stra");
            f.setAccessible(true);
            String value1 = (String) f.get(obj);
            System.out.println("load " + value1);
            f.set(obj, "hehehe");

            Method meth = c.getDeclaredMethod("getStra", null);
            meth.setAccessible(true);
            String value2 = (String) meth.invoke(obj, null);
            System.out.println("load " + value2);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * ��ȡ�������ͣ�����ֵ������Class[]��
     */
    public static Class[] getParamTypes(Class cls, String mName) {
        Class[] cs = null;
        /*
         * Note: ��������һ��ͨ��������Ƶ��õķ������Ƿ�public���� �����ڴ˴�ʹ����getDeclaredMethods()����
         */
        Method[] mtd = cls.getDeclaredMethods();
        for (int i = 0; i < mtd.length; i++) {
            if (!mtd[i].getName().equals(mName)) { // ����������Ҫ�Ĳ������������һ��ѭ��
                continue;
            }
            cs = mtd[i].getParameterTypes();
        }
        return cs;
    }

}
