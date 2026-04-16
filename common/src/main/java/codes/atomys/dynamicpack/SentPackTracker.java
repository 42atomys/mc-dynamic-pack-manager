package codes.atomys.dynamicpack;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import net.minecraft.network.Connection;

/**
 * Tracks which dynamic pack UUIDs have already been pushed to each live
 * connection. Used to suppress redundant {@link net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket}
 * sends, which would otherwise re-open the client's "Process" prompt on every
 * {@code /dynamicpack send} (Minecraft 26.x unconditionally re-prompts for
 * every push unless the player has globally allowed server packs).
 *
 * <p>Entries are stored against {@link Connection} with weak keys so dropped
 * connections are garbage-collected without explicit cleanup.</p>
 */
public final class SentPackTracker {

  private static final Map<Connection, Set<UUID>> SENT =
      Collections.synchronizedMap(new WeakHashMap<>());

  private SentPackTracker() {
  }

  /**
   * Marks the given pack UUID as pushed to the given connection.
   *
   * @param connection the connection the pack was pushed to
   * @param packUuid the UUID of the pack that was pushed
   */
  public static void markSent(final Connection connection, final UUID packUuid) {
    SENT.computeIfAbsent(connection, c -> Collections.synchronizedSet(new HashSet<>()))
        .add(packUuid);
  }

  /**
   * Returns whether the given pack UUID has already been pushed to the given
   * connection in this session.
   *
   * @param connection the connection to check
   * @param packUuid the UUID of the pack to check
   * @return {@code true} if the pack has already been pushed to the connection
   */
  public static boolean hasSent(final Connection connection, final UUID packUuid) {
    final Set<UUID> set = SENT.get(connection);
    return set != null && set.contains(packUuid);
  }

  /**
   * Forgets that the pack was sent to the given connection. Call after
   * sending a pop packet so the next push is not dedup'd away.
   *
   * @param connection the connection to forget the pack for
   * @param packUuid the UUID of the pack to forget
   */
  public static void forget(final Connection connection, final UUID packUuid) {
    final Set<UUID> set = SENT.get(connection);
    if (set != null) {
      set.remove(packUuid);
    }
  }

  /**
   * Forgets the pack on every known connection. Call when the pack is
   * refreshed or removed so the next send pushes it again to everyone.
   *
   * @param packUuid the UUID of the pack to forget on all connections
   */
  public static void forgetOnAll(final UUID packUuid) {
    synchronized (SENT) {
      for (final Set<UUID> set : SENT.values()) {
        set.remove(packUuid);
      }
    }
  }
}
