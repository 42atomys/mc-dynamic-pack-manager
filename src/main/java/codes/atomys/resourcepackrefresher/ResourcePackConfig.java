package codes.atomys.resourcepackrefresher;

import java.util.ArrayList;
import java.util.List;

import blue.endless.jankson.Comment;
import net.kyrptonaught.kyrptconfig.config.AbstractConfigFile;

public class ResourcePackConfig implements AbstractConfigFile {

  @Comment("Should all criteria be automatically revoked next time the command is executed.")
  Boolean autoRevoke = true;

  @Comment("List of all the resourcepacks configurations to use")
  List<RPOption> packs = new ArrayList<>();

  public static class RPOption {
    public String packname;

    public String url;

    public boolean required = true;

    public boolean hasPrompt = true;

    public String message = "plz use me";
  }
}
