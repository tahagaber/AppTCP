package util;

/**
 * Level 1: Port Utility
 * Generates a valid port number based on an academic ID.
 */
public class IDUtils {

    /**
     * Generates a port based on the given academic ID carefully following the rules:
     * - Last 5 digits
     * - If > 65535, use last 4 digits instead
     * - If < 1024, add 10000
     *
     * @param academicID The academic ID as a String
     * @return A valid network port integer
     */
    public static int generatePort(String academicID) {
        if (academicID == null || academicID.isEmpty()) return 10000;
        
        // Remove non-numeric characters for safety
        String cleanId = academicID.replaceAll("\\D", "");
        if (cleanId.length() < 4) {
            cleanId = String.format("%04d", Integer.parseInt(academicID));
        }

        int port;
        String portStr;
        
        // Take last 5 digits if possible
        if (cleanId.length() >= 5) {
            portStr = cleanId.substring(cleanId.length() - 5);
            port = Integer.parseInt(portStr);
            
            // If > 65535 -> take last 4 digits instead
            if (port > 65535) {
                portStr = cleanId.substring(cleanId.length() - 4);
                port = Integer.parseInt(portStr);
            }
        } else {
            portStr = cleanId; // Fallback to whatever size is available
            port = Integer.parseInt(portStr);
        }

        // If < 1024 -> add 10000
        if (port < 1024) {
            port += 10000;
        }
        
        return port;
    }

    public static void main(String[] args) {
        String testId = "2220550";
        System.out.println("Academic ID: " + testId);
        System.out.println("Generated Port: " + generatePort(testId));
    }
}
