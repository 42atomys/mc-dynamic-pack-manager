package codes.atomys.dynamicpack;

import codes.atomys.dynamicpack.utils.URLValidator;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundResourcePackPopPacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.repository.Pack;
import org.jetbrains.annotations.Nullable;

/**
 * A record class representing a dynamic resource pack.
 * <p>
 * The uuid of the pack is optional and defaults to a name-based uuid if not provided.
 * The hash of the pack is also optional and defaults to an empty string if not provided.
 * </p>
 *
 * @param packname the name of the pack
 * @param url the url of the pack
 * @param required if the pack is required
 * @param message the prompt message to display
 * @param uuid the uuid of the pack
 * @param hash the hash of the pack
 * @param version the cache-busting version stamp (epoch seconds). Stable across
 *     sends so that clients recognize the pack and skip the "Process" prompt
 *     after the first acceptance. Bump it via {@link #withRefreshedVersion()}
 *     to force clients to redownload.
 */
public record DynamicPack(String packname, String url, boolean required, Optional<String> message, UUID uuid, String hash, long version) {
  /**
   * This is the default pack used to initialize the configuration file if the
   * configuration file is not found (on the first run of the server).
   */
  public static final DynamicPack defaultpack = from(
    "dynamicpack_translations",
    "https://cdn.modrinth.com/data/ce851HU1/versions/ZdCyMNHL/DynamicPackLanguages-2.0.0.zip",
    true,
    Optional.of("The mod have dynamic resource packs itself, this is cool not ?"),
    Optional.empty(),
    Optional.empty()
  );

  /**
   * Create a new {@link Pack} instance from the given parameters.
   * <p>
   * The {@code uuid} parameter is optional and defaults to a name-based uuid if not provided.
   * The {@code hash} parameter is also optional and defaults to an empty string if not provided.
   * </p>
   *
   * @param packname the name of the pack
   * @param url the url of the pack
   * @param required if the pack is required
   * @param message the prompt message
   * @param uuid the uuid of the pack, or an empty optional if the uuid should be generated
   * @param hash the hash of the pack, or an empty optional if no hash is provided
   * @return a new {@link Pack} instance9
   */
  public static DynamicPack from(final String packname, final String url, final boolean required, final Optional<String> message,
      final Optional<UUID> uuid, final Optional<String> hash) {
    final UUID uUID = uuid.orElseGet(() -> UUID.nameUUIDFromBytes(url.getBytes(StandardCharsets.UTF_8)));

    return new DynamicPack(packname, url, required, message, uUID, hash.orElse(""), Instant.now().getEpochSecond());
  }

  /**
   * Returns true if the pack has a prompt (i.e. its message is not blank).
   * <p>
   * A pack has a prompt if its message is not blank.
   * </p>
   *
   * @return true if the pack has a prompt, false otherwise
   */
  public boolean hasPrompt() {
    return this.message().isPresent();
  }

  /**
   * Returns the pack's URL with a stable version string appended.
   * <p>
   * The version string is {@code v=<pack.version()>} (epoch seconds captured
   * at pack creation or last refresh). It stays identical across successive
   * {@code /dynamicpack send} invocations so that a client which already
   * accepted the pack does not see the "Process" prompt again. To force
   * clients to redownload (because the zip at the URL was replaced), call
   * {@link #withRefreshedVersion()} and persist the new pack.
   * </p>
   *
   * @return the pack's URL with a version string appended
   */
  public String versionnedUrl() {
    try {
      final URI uri = new URI(this.url());
      final String separator = uri.getQuery() != null ? "&" : "?";

      return this.url() + separator + "v=" + this.version();
    } catch (final URISyntaxException e) {
      e.printStackTrace();

      return this.url();
    }
  }

  /**
   * Validates the pack.
   * <p>
   * The pack is invalid if any of the following conditions are true:
   * </p>
   * <ul>
   *   <li>The packname contains a space.</li>
   *   <li>The URL is not absolute.</li>
   *   <li>The URL is not a valid resource pack URL.</li>
   *   <li>The UUID is not valid.</li>
   * </ul>
   * <p>
   * If the pack is invalid, an {@link InvalidDynamicPackException} is thrown.
   * </p>
   * <p>
   * This method is intended to be used by the configuration file to validate
   * the packs in the configuration file.
   * </p>
   *
   * @return true if the pack is valid, false otherwise
   * @throws InvalidDynamicPackException if the pack is invalid
   */
  public boolean validate() throws InvalidDynamicPackException {
    if (this.packname().contains(" ")) {
      throw InvalidDynamicPackException.of("commands.dynamicpackmanager.add.error.contains_space");
    }

    try {
      final URI uri = new URI(this.url());
      if (!uri.isAbsolute()) {
        throw InvalidDynamicPackException.of("commands.dynamicpackmanager.add.error.url_absolute");
      }
    } catch (final URISyntaxException e) {
      throw InvalidDynamicPackException.of("commands.dynamicpackmanager.add.error.invalid_url");
    }

    try {
      URLValidator.isResourcePackURLValid(this.url());
    } catch (final URLValidator.InvalidResourcePackURLException e) {
      throw new InvalidDynamicPackException(Component.translatable("commands.dynamicpackmanager.add.error.invalid_url").append(e.getComponent()));
    }

    if (this.uuid() == null) {
      throw InvalidDynamicPackException.of("commands.dynamicpackmanager.add.error.invalid_uuid");
    }

    return true;
  }

  /**
   * Creates a new DynamicPack instance with the given name.
   *
   * @param name the new name
   * @return a new DynamicPack instance with the given name
   */
  public DynamicPack withName(final String name) {
    return new DynamicPack(name, this.url(), this.required(), this.message(), this.uuid(), this.hash(), this.version());
  }

  /**
   * Creates a new DynamicPack instance with the given required flag.
   *
   * @param required the new required flag
   * @return a new DynamicPack instance with the given required flag
   */
  public DynamicPack withRequired(final boolean required) {
    return new DynamicPack(this.packname(), this.url(), required, this.message(), this.uuid(), this.hash(), this.version());
  }

  private DynamicPack withHash(final String hash) {
    return new DynamicPack(this.packname(), this.url(), this.required(), this.message(), this.uuid(), hash, this.version());
  }

  /**
   * Returns a copy of this pack with the version stamp refreshed to the
   * current epoch seconds. Use this to force clients to redownload the pack
   * (e.g. after replacing the zip at the same URL). The bumped version
   * invalidates the client-side HTTP cache and regenerates the cache-busted
   * URL returned by {@link #versionnedUrl()}.
   *
   * @return a new DynamicPack instance with a fresh version stamp
   */
  public DynamicPack withRefreshedVersion() {
    return new DynamicPack(this.packname(), this.url(), this.required(), this.message(), this.uuid(), this.hash(), Instant.now().getEpochSecond());
  }

  /**
   * Creates a new DynamicPack instance with the given message.
   *
   * @param message the new message, or an empty optional if no message should be displayed
   * @return a new DynamicPack instance with the given message
   */
  public DynamicPack withMessage(Optional<String> message) {
    if (message.isEmpty()) {
      message = Optional.empty();
    }

    return new DynamicPack(this.packname(), this.url(), this.required(), message, this.uuid(), this.hash(), this.version());
  }

  /**
   * Download the resourcepack into the tmp of the host and retrieve the remote hash.
   * <p>
   * The remote hash is stored into the dynamicpack.hash.zip file.
   * </p>
   * <p>
   * ! Actually this function is not used. !
   * </p>
   *
   * @return the remote hash of the pack
   */
  public DynamicPack withRemoteHash() {
    try {
      final File tmpFile = File.createTempFile("dynamicpack", ".zip");
      try {
        final URLConnection connection = new URI(this.versionnedUrl()).toURL().openConnection();
        Files.copy(connection.getInputStream(), tmpFile.toPath());
        final byte[] hashBytes = MessageDigest.getInstance("SHA-1").digest(Files.readAllBytes(tmpFile.toPath()));
        final StringBuilder sb = new StringBuilder();
        for (final byte b : hashBytes) {
          sb.append(String.format("%02x", b));
        }
        final String remoteHash = sb.toString();

        return this.withHash(remoteHash);
      } finally {
        Files.delete(tmpFile.toPath());
      }
    } catch (final Exception e) {
      return this;
    }
  }

  /**
   * Send a packet to all connections on the server to add a pack.
   * <p>
   * The packet is constructed using the pack's url, uuid and hash.
   * The uuid is generated from the pack's url if not provided.
   * The hash is an empty string if not provided.
   * </p>
   *
   * @param server the server to send the packet to
   * @return 0
   */
  public int sendAddPacketToAll(final MinecraftServer server, final @Nullable String customMessage) {
    final ClientboundResourcePackPushPacket packet = this.clientboundResourcePackPushPacket(customMessage);
    server.getConnection().getConnections().forEach(connection -> {
      if (SentPackTracker.hasSent(connection, this.uuid())) {
        return;
      }
      connection.send(packet);
      SentPackTracker.markSent(connection, this.uuid());
    });
    return 0;
  }

  /**
   * Send a packet to all connections in the given target list to add a pack.
   * <p>
   * The packet is constructed using the pack's url, uuid and hash.
   * The uuid is generated from the pack's url if not provided.
   * The hash is an empty string if not provided.
   * </p>
   *
   * @param server the server to send the packet to
   * @param targets the collection of targets to send the packet to
   * @return 0
   */
  public int sendAddPacketToTargets(final MinecraftServer server, final Collection<ServerPlayer> targets, final @Nullable String customMessage) {
    final ClientboundResourcePackPushPacket packet = this.clientboundResourcePackPushPacket(customMessage);
    for (final ServerPlayer serverplayer : targets) {
      final net.minecraft.network.Connection connection = serverplayer.connection.connection;
      if (SentPackTracker.hasSent(connection, this.uuid())) {
        continue;
      }
      serverplayer.connection.send(packet);
      SentPackTracker.markSent(connection, this.uuid());
    }

    return 0;
  }

  /**
   * Send a packet to remove a pack to all connections on the server.
   *
   * @param server the server to send the packet to
   * @return 0
   */
  public int sendRemovePacketToAll(final MinecraftServer server) {
    this.sendToAllConnections(server, this.clientboundResourcePackPopPacket());
    SentPackTracker.forgetOnAll(this.uuid());
    return 0;
  }

  /**
   * Send a packet to remove a pack to the given targets on the server.
   *
   * @param server the server to send the packet to
   * @param targets the collection of targets to send the packet to
   * @return 0
   */
  public int sendRemovePacketToTargets(final MinecraftServer server, final Collection<ServerPlayer> targets) {
    for (final ServerPlayer serverplayer : targets) {
      serverplayer.connection.send(this.clientboundResourcePackPopPacket());
      SentPackTracker.forget(serverplayer.connection.connection, this.uuid());
    }

    return 0;
  }

  /**
   * Send a packet to all connections on the server.
   *
   * @param server the server to send the packet to
   * @param packet the packet to send
   */
  private void sendToAllConnections(final MinecraftServer server, final Packet<?> packet) {
    server.getConnection().getConnections().forEach(connection -> connection.send(packet));
  }

  /**
   * Return a {@link ClientboundResourcePackPushPacket} constructed from the
   * information stored in this pack.
   * <p>
   * The packet is constructed using the pack's uuid, url, hash and required status.
   * The prompt message is also included if the pack has a prompt.
   * </p>
   *
   * @return a new {@link ClientboundResourcePackPushPacket}
   */
  private ClientboundResourcePackPushPacket clientboundResourcePackPushPacket(final @Nullable String customMessage) {
    final Component messageComponent = customMessage != null ?
      Component.translatable(customMessage) :
      Component.translatable(this.message().orElse("text.dynamicpackmanager.default_prompt"));

    return new ClientboundResourcePackPushPacket(
      this.uuid(),
      this.versionnedUrl(),
      this.hash(),
      this.required(),
      Optional.of(messageComponent)
    );
  }

  /**
   * Return a {@link ClientboundResourcePackPopPacket} constructed from the
   * uuid of this pack.
   * <p>
   * The packet is constructed using the pack's uuid.
   * </p>
   *
   * @return a new {@link ClientboundResourcePackPopPacket}
   */
  private ClientboundResourcePackPopPacket clientboundResourcePackPopPacket() {
    return new ClientboundResourcePackPopPacket(Optional.of(this.uuid()));
  }
}
