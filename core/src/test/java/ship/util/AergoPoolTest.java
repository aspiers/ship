package ship.util;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import hera.api.AergoApi;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.strategy.ConnectStrategy;
import hera.strategy.NettyConnectStrategy;
import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import ship.AbstractTestCase;

public class AergoPoolTest extends AbstractTestCase {

  @Test
  @SuppressWarnings("unchecked")
  @PrepareForTest(NettyChannelBuilder.class)
  public void testBorrowResource() throws Exception {
    // Given
    final NettyConnectStrategy connectStrategy = mock(NettyConnectStrategy.class);
    final NettyChannelBuilder managedChannel = mock(NettyChannelBuilder.class);
    whenNew(NettyConnectStrategy.class).withAnyArguments().thenReturn(connectStrategy);
    when(connectStrategy.connect()).thenReturn(managedChannel);

    // When
    final AergoPool pool = new AergoPool(randomUUID().toString());
    // Then
    assertNotNull(pool.borrowResource());
  }

  @Test
  @PrepareForTest(AergoClientBuilder.class)
  @SuppressWarnings("unchecked")
  public void testReturnResource() throws Exception {
    // Given
    final ConnectStrategy<ManagedChannel> connectStrategy = mock(ConnectStrategy.class);
    final AergoClient client = mock(AergoClient.class);
    whenNew(AergoClient.class).withAnyArguments().thenReturn(client);

    // When
    final AergoPool pool = new AergoPool(randomUUID().toString());
    final AergoApi api = pool.borrowResource();
    pool.returnResource(api);

    // Then
    verify((AergoClient) api).close();
  }

}