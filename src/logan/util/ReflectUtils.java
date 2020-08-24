package logan.util;

public class ReflectUtils {

    /**
     * Gets a specified Bukkit class in <code>subPackage</code>.
     * A sub-package is a package contained within <code>org.bukkit</code>.
     * Therefore a valid sub-package would be <code>inventory</code> to
     * retrieve a class in the package <code>org.bukkit.inventory</code>.
     * <p>
     * If the class isn't in a sub-package the specify it as an empty string
     * or <tt>null</tt>.
     */
    public static Class<?> getBukkitClass(String subPackage, String className) {
        String nameString;
        if (subPackage == null || subPackage.isEmpty())
            nameString = "org.bukkit." + className;
        else
            nameString = "org.bukkit." + subPackage + "." + className;

        Class<?> clazz = null;
        try {
            clazz = Class.forName(nameString);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clazz;
    }
}
