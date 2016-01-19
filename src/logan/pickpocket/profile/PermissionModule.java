package logan.pickpocket.profile;

/**
 * Created by Tre on 1/17/2016.
 */
public class PermissionModule {

    private boolean isStealExempt = false;
    private boolean canBypass = false;
    private boolean isAdmin = false;

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public void setCanBypass(boolean canBypass) {
        this.canBypass = canBypass;
    }

    public void setStealExempt(boolean isStealExempt) {
        this.isStealExempt = isStealExempt;
    }

    public boolean isStealExempt() {
        return isStealExempt;
    }

    public boolean canBypass() {
        return canBypass;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

}
