import React, { useState, useEffect } from 'react';
import { Users, Clock, AlertTriangle, CheckCircle2, ShieldAlert, Sparkles, RefreshCw, XCircle } from 'lucide-react';
import { useWaitlistSocket, WaitlistEventPayload } from '../../hooks/useWaitlistSocket';

export interface WaitlistEntry {
  id: string;
  restaurantId: string;
  restaurantName: string;
  partySize: number;
  queuePosition: number;
  estWaitMins: number;
  isPriority: boolean;
  status: 'QUEUED' | 'OFFERED' | 'ACCEPTED' | 'CANCELLED' | 'EXPIRED';
  expiresAt?: string;
  createdAt: string;
}

interface WaitlistDashboardWidgetProps {
  initialEntry: WaitlistEntry;
  userId: string;
  apiBaseUrl?: string;
  serverUrl?: string;
  authToken?: string;
  onEntryStatusChange?: (updatedEntry: WaitlistEntry) => void;
}

export const WaitlistDashboardWidget: React.FC<WaitlistDashboardWidgetProps> = ({
  initialEntry,
  userId,
  apiBaseUrl = 'http://localhost:3000',
  serverUrl = 'http://localhost:3000',
  authToken,
  onEntryStatusChange,
}) => {
  const [entry, setEntry] = useState<WaitlistEntry>(initialEntry);
  const [secondsRemaining, setSecondsRemaining] = useState<number | null>(null);
  const [isClaiming, setIsClaiming] = useState(false);
  const [isLeaving, setIsLeaving] = useState(false);
  const [actionMessage, setActionMessage] = useState<string | null>(null);

  // Real-time WebSocket hook connection
  const { isConnected } = useWaitlistSocket(
    serverUrl,
    entry.restaurantId,
    userId,
    (payload: WaitlistEventPayload) => {
      if (payload.entryId === entry.id) {
        if (payload.action === 'OFFERED') {
          const expiresAt = payload.expiresAt ? new Date(payload.expiresAt) : new Date(Date.now() + 5 * 60 * 1000);
          const updated = {
            ...entry,
            status: 'OFFERED' as const,
            expiresAt: expiresAt.toISOString(),
          };
          setEntry(updated);
          onEntryStatusChange?.(updated);
        } else if (payload.action === 'ACCEPTED') {
          const updated = { ...entry, status: 'ACCEPTED' as const };
          setEntry(updated);
          onEntryStatusChange?.(updated);
        } else if (payload.action === 'EXPIRED') {
          const updated = { ...entry, status: 'EXPIRED' as const };
          setEntry(updated);
          onEntryStatusChange?.(updated);
        } else if (payload.action === 'CANCELLED') {
          const updated = { ...entry, status: 'CANCELLED' as const };
          setEntry(updated);
          onEntryStatusChange?.(updated);
        }
      }
    },
  );

  // 5-Minute Countdown Timer logic for OFFERED state
  useEffect(() => {
    if (entry.status !== 'OFFERED' || !entry.expiresAt) {
      setSecondsRemaining(null);
      return;
    }

    const targetTime = new Date(entry.expiresAt).getTime();

    const updateTimer = () => {
      const now = Date.now();
      const diffSecs = Math.max(0, Math.floor((targetTime - now) / 1000));
      setSecondsRemaining(diffSecs);

      if (diffSecs <= 0) {
        setEntry((prev) => ({ ...prev, status: 'EXPIRED' }));
      }
    };

    updateTimer();
    const interval = setInterval(updateTimer, 1000);

    return () => clearInterval(interval);
  }, [entry.status, entry.expiresAt]);

  // Format MM:SS
  const formatTime = (secs: number) => {
    const mins = Math.floor(secs / 60);
    const remainder = secs % 60;
    return `${mins.toString().padStart(2, '0')}:${remainder.toString().padStart(2, '0')}`;
  };

  const handleClaimTable = async () => {
    setIsClaiming(true);
    setActionMessage(null);

    try {
      const res = await fetch(`${apiBaseUrl}/api/v1/waitlist/${entry.id}/claim`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...(authToken ? { Authorization: `Bearer ${authToken}` } : {}),
        },
      });

      if (!res.ok) {
        const error = await res.json();
        throw new Error(error.message || 'Failed to claim table');
      }

      const updated = await res.json();
      setEntry(updated);
      setActionMessage('Table successfully claimed! Proceed to host stand.');
      onEntryStatusChange?.(updated);
    } catch (err: any) {
      setActionMessage(`Error: ${err.message}`);
    } finally {
      setIsClaiming(false);
    }
  };

  const handleLeaveWaitlist = async () => {
    setIsLeaving(true);
    setActionMessage(null);

    try {
      const res = await fetch(`${apiBaseUrl}/api/v1/waitlist/${entry.id}/leave`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          ...(authToken ? { Authorization: `Bearer ${authToken}` } : {}),
        },
      });

      if (!res.ok) {
        const error = await res.json();
        throw new Error(error.message || 'Failed to leave queue');
      }

      const updated = await res.json();
      setEntry(updated);
      onEntryStatusChange?.(updated);
    } catch (err: any) {
      setActionMessage(`Error: ${err.message}`);
    } finally {
      setIsLeaving(false);
    }
  };

  return (
    <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-xl max-w-md w-full text-slate-100">
      {/* Real-time connection badge */}
      <div className="flex items-center justify-between border-b border-slate-800 pb-4 mb-4">
        <div>
          <h3 className="text-base font-bold text-white">{entry.restaurantName}</h3>
          <p className="text-xs text-slate-400">Live Waitlist Tracker</p>
        </div>
        <div className="flex items-center gap-2">
          <span
            className={`w-2.5 h-2.5 rounded-full ${
              isConnected ? 'bg-emerald-500 animate-pulse' : 'bg-slate-500'
            }`}
          />
          <span className="text-[11px] font-semibold text-slate-400">
            {isConnected ? 'Real-time Live' : 'Reconnecting...'}
          </span>
        </div>
      </div>

      {/* Main Status Display */}
      {entry.status === 'QUEUED' && (
        <div className="space-y-4">
          <div className="flex items-center justify-around bg-slate-800/60 p-4 rounded-xl border border-slate-700/50">
            <div className="text-center">
              <span className="block text-xs uppercase font-semibold text-slate-400">Queue Spot</span>
              <span className="text-3xl font-extrabold text-amber-400">#{entry.queuePosition}</span>
            </div>
            <div className="h-10 w-[1px] bg-slate-700" />
            <div className="text-center">
              <span className="block text-xs uppercase font-semibold text-slate-400">Est. Wait</span>
              <span className="text-3xl font-extrabold text-slate-100">{entry.estWaitMins}m</span>
            </div>
            <div className="h-10 w-[1px] bg-slate-700" />
            <div className="text-center">
              <span className="block text-xs uppercase font-semibold text-slate-400">Party</span>
              <span className="text-3xl font-extrabold text-slate-100">{entry.partySize}</span>
            </div>
          </div>

          {entry.isPriority && (
            <div className="px-3 py-2 bg-amber-500/10 border border-amber-500/20 text-amber-300 rounded-lg text-xs flex items-center gap-2 font-medium">
              <Sparkles className="w-4 h-4 text-amber-400 shrink-0" />
              <span>Priority Placement Active: Expedited Queue Handling</span>
            </div>
          )}

          {/* Progress Bar */}
          <div>
            <div className="flex justify-between text-xs text-slate-400 mb-1">
              <span>Queue Status</span>
              <span>Waiting for Table</span>
            </div>
            <div className="w-full bg-slate-800 rounded-full h-2.5 overflow-hidden">
              <div
                className="bg-amber-500 h-2.5 rounded-full transition-all duration-500"
                style={{
                  width: `${Math.max(15, 100 - (entry.queuePosition - 1) * 20)}%`,
                }}
              />
            </div>
          </div>

          <button
            onClick={handleLeaveWaitlist}
            disabled={isLeaving}
            className="w-full py-2.5 rounded-xl border border-slate-700 hover:bg-slate-800 text-slate-400 hover:text-slate-200 text-xs font-semibold transition-colors flex items-center justify-center gap-2"
          >
            <XCircle className="w-4 h-4" />
            <span>Leave Waitlist Queue</span>
          </button>
        </div>
      )}

      {/* OFFERED State - 5-Minute Countdown */}
      {entry.status === 'OFFERED' && (
        <div className="space-y-4">
          <div className="bg-amber-500/15 border border-amber-500/30 p-5 rounded-xl text-center">
            <div className="inline-flex p-3 bg-amber-500/20 text-amber-400 rounded-full mb-2 animate-bounce">
              <Clock className="w-6 h-6" />
            </div>
            <h4 className="text-lg font-bold text-amber-400">Your Table is Ready!</h4>
            <p className="text-xs text-slate-300 mt-1">
              Please claim your table within the 5-minute expiry window.
            </p>

            {/* Countdown Clock */}
            {secondsRemaining !== null && (
              <div className="mt-3 py-2 px-4 bg-slate-950/80 rounded-xl border border-amber-500/30 inline-block">
                <span className="text-3xl font-mono font-black tracking-widest text-amber-400">
                  {formatTime(secondsRemaining)}
                </span>
              </div>
            )}
          </div>

          <button
            onClick={handleClaimTable}
            disabled={isClaiming || (secondsRemaining !== null && secondsRemaining <= 0)}
            className="w-full py-3 bg-amber-500 hover:bg-amber-400 text-slate-950 font-bold text-sm rounded-xl shadow-lg shadow-amber-500/20 transition-all flex items-center justify-center gap-2"
          >
            <CheckCircle2 className="w-5 h-5" />
            <span>{isClaiming ? 'Claiming Table...' : 'Claim Table Now'}</span>
          </button>
        </div>
      )}

      {/* ACCEPTED State */}
      {entry.status === 'ACCEPTED' && (
        <div className="p-5 bg-emerald-500/10 border border-emerald-500/20 rounded-xl text-center space-y-2">
          <div className="inline-flex p-3 bg-emerald-500/20 text-emerald-400 rounded-full mb-1">
            <CheckCircle2 className="w-8 h-8" />
          </div>
          <h4 className="text-lg font-bold text-emerald-400">Table Claimed!</h4>
          <p className="text-xs text-slate-300">
            Welcome to {entry.restaurantName}. Please present yourself to the host stand.
          </p>
        </div>
      )}

      {/* EXPIRED State */}
      {entry.status === 'EXPIRED' && (
        <div className="p-5 bg-rose-500/10 border border-rose-500/20 rounded-xl text-center space-y-2">
          <div className="inline-flex p-3 bg-rose-500/20 text-rose-400 rounded-full mb-1">
            <AlertTriangle className="w-8 h-8" />
          </div>
          <h4 className="text-lg font-bold text-rose-400">Offer Expired</h4>
          <p className="text-xs text-slate-300">
            The 5-minute claim window elapsed. Please re-join the waitlist queue if needed.
          </p>
        </div>
      )}

      {actionMessage && (
        <p className="text-xs text-center font-medium mt-3 text-amber-300">{actionMessage}</p>
      )}
    </div>
  );
};
