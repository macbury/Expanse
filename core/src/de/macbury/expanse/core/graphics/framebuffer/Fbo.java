package de.macbury.expanse.core.graphics.framebuffer;

/**
 * This enum contains all fbos used by engine
 */
public enum Fbo {
  /**
   * In this framebuffer are rendered all objects with shadows etc before blur an blooms
   */
  MainColor("expanse:main-color");
  private final String namespace;
  Fbo(String namespace) {
    this.namespace = namespace;
  }

  public String getNamespace() {
    return namespace;
  }
}
