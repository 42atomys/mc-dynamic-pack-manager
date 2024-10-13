package codes.atomys.dynamicpack;

import net.minecraft.network.chat.Component;

/**
 * Exception thrown when a dynamic pack is invalid.
 */
public class InvalidDynamicPackException extends RuntimeException {
  private Component compoment;

  /**
   * Constructs a new InvalidDynamicPackException with the specified detail message.
   *
   * @param component the component that can be used to display the error message
   */
  public InvalidDynamicPackException(final Component component) {
    super(component.toString());
    this.compoment = component;
  }

  /**
   * Creates a new InvalidDynamicPackException with a translatable component.
   *
   * @param translatable the translation key for the component
   * @return a new InvalidDynamicPackException with a translatable component
   */
  public static InvalidDynamicPackException of(final String translatable) {
    return new InvalidDynamicPackException(Component.translatable(translatable));
  }

  /**
   * Gets the component that can be used to display the error message.
   *
   * @return the component that can be used to display the error message
   */
  public Component getComponent() {
    return this.compoment;
  }
}
