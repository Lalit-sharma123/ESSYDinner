import React from 'react';
import { Car, Zap, Accessibility, ShieldCheck, CheckCircle, AlertTriangle } from 'lucide-react';

export interface ParkingSlot {
  id: string;
  slotNumber: string;
  slotType: 'STANDARD' | 'EV_CHARGING' | 'ACCESSIBLE' | 'VIP';
  state: 'Available' | 'Occupied' | 'Reserved' | 'Disabled' | 'EV_Charging' | 'VIP';
  occupiedBy?: string;
}

export interface ParkingLotSummary {
  id: string;
  lotName: string;
  totalCapacity: number;
  availableCount: number;
  evAvailableCount: number;
  accessibleSlotsCount: number;
  slots: ParkingSlot[];
}

interface LiveParkingCardProps {
  lot?: ParkingLotSummary;
  onSlotStateChange?: (slotId: string, newState: string) => void;
}

export const LiveParkingCard: React.FC<LiveParkingCardProps> = ({ lot, onSlotStateChange }) => {
  const defaultLot: ParkingLotSummary = lot || {
    id: 'lot_1',
    lotName: 'Restaurant Valet & Guest Parking',
    totalCapacity: 12,
    availableCount: 5,
    evAvailableCount: 2,
    accessibleSlotsCount: 1,
    slots: [
      { id: 'p1', slotNumber: 'P-01', slotType: 'ACCESSIBLE', state: 'Available' },
      { id: 'p2', slotNumber: 'P-02', slotType: 'EV_CHARGING', state: 'Available' },
      { id: 'p3', slotNumber: 'P-03', slotType: 'EV_CHARGING', state: 'Occupied', occupiedBy: 'Tesla Model Y' },
      { id: 'p4', slotNumber: 'P-04', slotType: 'VIP', state: 'Reserved', occupiedBy: 'VIP Guest' },
      { id: 'p5', slotNumber: 'P-05', slotType: 'STANDARD', state: 'Available' },
      { id: 'p6', slotNumber: 'P-06', slotType: 'STANDARD', state: 'Occupied' },
    ],
  };

  return (
    <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 text-slate-100 shadow-xl max-w-xl w-full space-y-6">
      <div className="flex items-center justify-between border-b border-slate-800 pb-4">
        <div className="flex items-center gap-3">
          <span className="p-2.5 bg-sky-500/10 text-sky-400 rounded-xl">
            <Car className="w-6 h-6" />
          </span>
          <div>
            <h3 className="text-lg font-bold text-white">{defaultLot.lotName}</h3>
            <p className="text-xs text-slate-400">Live sensor parking availability feed</p>
          </div>
        </div>

        <div className="text-right">
          <span className="text-2xl font-black text-emerald-400">{defaultLot.availableCount}</span>
          <span className="text-xs text-slate-400 font-semibold block">/ {defaultLot.totalCapacity} Spaces Open</span>
        </div>
      </div>

      {/* Specialty Badges */}
      <div className="grid grid-cols-3 gap-3 text-xs">
        <div className="p-3 bg-slate-800/80 rounded-xl border border-slate-700/60 flex items-center gap-2">
          <Zap className="w-4 h-4 text-emerald-400 shrink-0" />
          <div>
            <p className="font-bold text-white">{defaultLot.evAvailableCount} EV Chargers</p>
            <p className="text-[10px] text-slate-400">Ready for fast charge</p>
          </div>
        </div>

        <div className="p-3 bg-slate-800/80 rounded-xl border border-slate-700/60 flex items-center gap-2">
          <Accessibility className="w-4 h-4 text-sky-400 shrink-0" />
          <div>
            <p className="font-bold text-white">{defaultLot.accessibleSlotsCount} Accessible</p>
            <p className="text-[10px] text-slate-400">Wheelchair ramp</p>
          </div>
        </div>

        <div className="p-3 bg-slate-800/80 rounded-xl border border-slate-700/60 flex items-center gap-2">
          <ShieldCheck className="w-4 h-4 text-amber-400 shrink-0" />
          <div>
            <p className="font-bold text-white">Valet Attendant</p>
            <p className="text-[10px] text-slate-400">On duty</p>
          </div>
        </div>
      </div>

      {/* Slot Grid */}
      <div className="grid grid-cols-3 gap-3">
        {defaultLot.slots.map((s) => {
          const isAvailable = s.state === 'Available';
          return (
            <div
              key={s.id}
              onClick={() => onSlotStateChange?.(s.id, isAvailable ? 'Occupied' : 'Available')}
              className={`p-3 rounded-xl border-2 transition-all cursor-pointer flex flex-col justify-between ${
                isAvailable
                  ? 'bg-emerald-500/10 border-emerald-500/40 text-emerald-300 hover:border-emerald-400'
                  : 'bg-rose-500/10 border-rose-500/40 text-rose-300 hover:border-rose-400'
              }`}
            >
              <div className="flex justify-between items-center text-xs font-bold">
                <span>{s.slotNumber}</span>
                {s.slotType === 'EV_CHARGING' && <Zap className="w-3.5 h-3.5 text-emerald-400" />}
                {s.slotType === 'ACCESSIBLE' && <Accessibility className="w-3.5 h-3.5 text-sky-400" />}
              </div>

              <div className="mt-3 flex items-center justify-between text-[11px] font-semibold">
                <span>{s.state}</span>
                {isAvailable ? <CheckCircle className="w-3.5 h-3.5" /> : <AlertTriangle className="w-3.5 h-3.5" />}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
