package top.syshub.tpp.modules;

public class ConfigClass {

    public TppConfig tpp = new TppConfig();
    public static class TppConfig {
        public boolean enabled = false;
        public int cooldown = 0;
        Target target = Target.teammates;
        public enum Target {
            teammates,
            allplayers
        }
    }
}
