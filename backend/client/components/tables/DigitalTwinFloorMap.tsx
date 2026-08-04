import React, { useState } from 'react';
import { Layout, Users, Sparkles, AlertCircle, RefreshCw, Layers } from 'lucide-react';

export interface TableItem {
  id: string;
  tableNumber: string;
  capacity: number;
  status: 'AVAILABLE' | 'RESERVED' | 'DINING' | 'CLEANING' | 'BLOCKED' | 'OUT_OF_SERVICE';
  positionX: number;
  positionY: number;
  shape: string;
  mergedWith?: string;
}

export interface FloorLayout {
  id: string;
  floorName: string;
  level: number;
  tables: TableItem[];
}

interface DigitalTwinFloorMapProps {
  restaurantId: string;
  floors: FloorLayout[];
  onStatusChange?: (tableId: string, newStatus: string) => void;
  onCleanComplete?: (tableId: string) => void;
}

export const DigitalTwinFloorMap: React.FC<DigitalTwinFloorMapProps> = ({
  restaurantId,
  floors,
  onStatusChange,
  onCleanComplete,
}) => {
  const [selectedFloorIndex, setSelectedFloorIndex] = useState(0);
  const [selectedTable, setSelectedTable] = useState<TableItem | null>(null);

  const currentFloor = floors[selectedFloorIndex] || {
    id: 'default',
    floorName: 'Main Dining Floor',
    level: 1,
    tables: [
      { id: 't1', tableNumber: 'T-101', capacity: 4, status: 'AVAILABLE', positionX: 50, positionY: 50, shape: 'RECTANGLE' },
      { id: 't2', tableNumber: 'T-102', capacity: 2, status: 'DINING', positionX: 200, positionY: 50, shape: 'CIRCLE' },
      { id: 't3', tableNumber: 'T-103', capacity: 6, status: 'RESERVED', positionX: 350, positionY: 50, shape: 'RECTANGLE' },
      { id: 't4', tableNumber: 'T-104', capacity: 4, status: 'CLEANING', positionX: 50, positionY: 200, shape: 'SQUARE' },
    ],
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'AVAILABLE':
        return 'bg-emerald-500/20 border-emerald-500 text-emerald-300 shadow-emerald-500/10';
      case 'RESERVED':
        return 'bg-amber-500/20 border-amber-500 text-amber-300 shadow-amber-500/10';
      case 'DINING':
        return 'bg-sky-500/20 border-sky-500 text-sky-300 shadow-sky-500/10';
      case 'CLEANING':
        return 'bg-purple-500/20 border-purple-500 text-purple-300 shadow-purple-500/10 animate-pulse';
      case 'BLOCKED':
        return 'bg-rose-500/20 border-rose-500 text-rose-300';
      default:
        return 'bg-slate-700 border-slate-600 text-slate-400';
    }
  };

  return (
    <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 text-slate-100 shadow-2xl max-w-4xl w-full space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between border-b border-slate-800 pb-4">
        <div className="flex items-center gap-3">
          <span className="p-2.5 bg-sky-500/10 text-sky-400 rounded-xl">
            <Layout className="w-6 h-6" />
          </span>
          <div>
            <h3 className="text-lg font-bold text-white flex items-center gap-2">
              Digital Twin Floor Map
              <span className="text-[10px] uppercase font-extrabold px-2 py-0.5 bg-emerald-500/20 text-emerald-400 rounded-full border border-emerald-500/30">
                Live Socket Sync
              </span>
            </h3>
            <p className="text-xs text-slate-400">Real-time table occupancy status map</p>
          </div>
        </div>

        {/* Floor selector */}
        <div className="flex items-center gap-2 bg-slate-800 p-1.5 rounded-xl border border-slate-700">
          {floors.map((fl, idx) => (
            <button
              key={fl.id}
              onClick={() => setSelectedFloorIndex(idx)}
              className={`px-3 py-1.5 rounded-lg text-xs font-bold transition-all ${
                selectedFloorIndex === idx
                  ? 'bg-amber-500 text-slate-950 shadow-md'
                  : 'text-slate-400 hover:text-white'
              }`}
            >
              {fl.floorName}
            </button>
          ))}
        </div>
      </div>

      {/* State Legend */}
      <div className="flex flex-wrap items-center gap-4 text-xs bg-slate-800/50 p-3 rounded-xl border border-slate-800">
        <span className="flex items-center gap-1.5 font-semibold text-emerald-400">
          <span className="w-2.5 h-2.5 rounded-full bg-emerald-500" /> Available
        </span>
        <span className="flex items-center gap-1.5 font-semibold text-amber-400">
          <span className="w-2.5 h-2.5 rounded-full bg-amber-500" /> Reserved
        </span>
        <span className="flex items-center gap-1.5 font-semibold text-sky-400">
          <span className="w-2.5 h-2.5 rounded-full bg-sky-500" /> Dining
        </span>
        <span className="flex items-center gap-1.5 font-semibold text-purple-400">
          <span className="w-2.5 h-2.5 rounded-full bg-purple-500" /> Cleaning Queue
        </span>
        <span className="flex items-center gap-1.5 font-semibold text-rose-400">
          <span className="w-2.5 h-2.5 rounded-full bg-rose-500" /> Blocked
        </span>
      </div>

      {/* Interactive Canvas Grid */}
      <div className="relative min-h-[380px] bg-slate-950 rounded-2xl border border-slate-800 p-6 overflow-hidden grid grid-cols-3 gap-6">
        {currentFloor.tables.map((t) => (
          <div
            key={t.id}
            onClick={() => setSelectedTable(t)}
            className={`p-5 rounded-2xl border-2 transition-all cursor-pointer flex flex-col justify-between shadow-lg hover:scale-105 ${getStatusColor(
              t.status,
            )}`}
          >
            <div className="flex justify-between items-start">
              <span className="text-base font-extrabold">{t.tableNumber}</span>
              <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-slate-900/80 text-white uppercase border border-slate-700">
                {t.status}
              </span>
            </div>

            <div className="mt-4 flex items-center justify-between text-xs opacity-90">
              <span className="flex items-center gap-1 font-semibold">
                <Users className="w-3.5 h-3.5" />
                {t.capacity} Seats
              </span>
              <span className="text-[10px] font-medium">{t.shape}</span>
            </div>
          </div>
        ))}
      </div>

      {/* Selected Table Control Modal */}
      {selectedTable && (
        <div className="p-4 bg-slate-800 border border-slate-700 rounded-xl flex items-center justify-between">
          <div>
            <h4 className="text-sm font-bold text-white">Table {selectedTable.tableNumber} Selected</h4>
            <p className="text-xs text-slate-400">Capacity: {selectedTable.capacity} Guests | Current Status: {selectedTable.status}</p>
          </div>
          <div className="flex items-center gap-2">
            {selectedTable.status === 'CLEANING' && (
              <button
                onClick={() => onCleanComplete?.(selectedTable.id)}
                className="px-3 py-1.5 bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs rounded-lg transition-colors"
              >
                Complete Cleaning
              </button>
            )}
            <button
              onClick={() => onStatusChange?.(selectedTable.id, 'DINING')}
              className="px-3 py-1.5 bg-sky-500 hover:bg-sky-400 text-slate-950 font-bold text-xs rounded-lg transition-colors"
            >
              Set Dining
            </button>
            <button
              onClick={() => setSelectedTable(null)}
              className="px-3 py-1.5 bg-slate-700 hover:bg-slate-600 text-slate-300 text-xs rounded-lg"
            >
              Close
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
