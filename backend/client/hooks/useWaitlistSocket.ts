import { useEffect, useState } from 'react';
import { io, Socket } from 'socket.io-client';

export interface WaitlistEventPayload {
  action: 'JOINED' | 'OFFERED' | 'ACCEPTED' | 'EXPIRED' | 'CANCELLED';
  entryId: string;
  queuePosition?: number;
  userId?: string;
  expiresAt?: string;
}

export const useWaitlistSocket = (
  serverUrl: string,
  restaurantId: string,
  userId: string,
  onEvent?: (payload: WaitlistEventPayload) => void,
) => {
  const [socket, setSocket] = useState<Socket | null>(null);
  const [isConnected, setIsConnected] = useState(false);

  useEffect(() => {
    const socketInstance = io(serverUrl, {
      transports: ['websocket'],
      autoConnect: true,
    });

    socketInstance.on('connect', () => {
      setIsConnected(true);
      // Join waitlist room for specific restaurant
      socketInstance.emit('joinRoom', `waitlist_${restaurantId}`);
    });

    socketInstance.on('disconnect', () => {
      setIsConnected(false);
    });

    socketInstance.on('waitlistUpdated', (payload: WaitlistEventPayload) => {
      if (onEvent) {
        onEvent(payload);
      }
    });

    setSocket(socketInstance);

    return () => {
      socketInstance.disconnect();
    };
  }, [serverUrl, restaurantId, userId]);

  return { socket, isConnected };
};
