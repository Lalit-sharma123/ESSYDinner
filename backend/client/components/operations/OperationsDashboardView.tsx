import React from 'react';
import { DollarSign, Users, UtensilsCrossed, Clock, AlertTriangle, TrendingUp, Sparkles, Heart } from 'lucide-react';

export interface OperationsMetricsData {
  restaurantId: string;
  tables: {
    total: number;
    occupied: number;
    cleaning: number;
    available: number;
    occupancyPercent: number;
  };
  kitchen: {
    activeOrdersCount: number;
    kitchenLoadPercent: number;
  };
  todayMetrics: {
    reservationsCount: number;
    walkInsCount: number;
    waitlistQueueLength: number;
    avgWaitTimeMins: number;
    avgDiningTimeMins: number;
    todayRevenue: number;
    revenueThisWeek: number;
    noShowRate: number;
    cancellationRate: number;
  };
  parking: {
    totalSlots: number;
    occupiedSlots: number;
    utilizationPercent: number;
  };
}

interface OperationsDashboardViewProps {
  metrics?: OperationsMetricsData;
}

export const OperationsDashboardView: React.FC<OperationsDashboardViewProps> = ({ metrics }) => {
  const data: OperationsMetricsData = metrics || {
    restaurantId: 'rest_1',
    tables: { total: 15, occupied: 11, cleaning: 2, available: 2, occupancyPercent: 73 },
    kitchen: { activeOrdersCount: 14, kitchenLoadPercent: 70 },
    todayMetrics: {
      reservationsCount: 22,
      walkInsCount: 16,
      waitlistQueueLength: 4,
      avgWaitTimeMins: 15,
      avgDiningTimeMins: 48,
      todayRevenue: 3840.5,
      revenueThisWeek: 24500.0,
      noShowRate: 3.5,
      cancellationRate: 4.8,
    },
    parking: { totalSlots: 20, occupiedSlots: 14, utilizationPercent: 70 },
  };

  return (
    <div className="bg-slate-950 border border-slate-800 rounded-2xl p-6 text-slate-100 shadow-2xl space-y-6 max-w-5xl w-full">
      {/* Title Header */}
      <div className="flex items-center justify-between border-b border-slate-800 pb-4">
        <div>
          <h2 className="text-xl font-black text-white flex items-center gap-2">
            Restaurant Operations Command Dashboard
            <span className="w-2.5 h-2.5 rounded-full bg-emerald-500 animate-ping" />
          </h2>
          <p className="text-xs text-slate-400">Live operational intelligence & KPI metrics</p>
        </div>
        <div className="text-right">
          <span className="text-xs font-bold text-amber-400 block">Today's Revenue</span>
          <span className="text-2xl font-black text-white">${data.todayMetrics.todayRevenue.toLocaleString()}</span>
        </div>
      </div>

      {/* KPI Cards Grid */}
      <div className="grid grid-cols-4 gap-4 text-xs">
        {/* Occupancy Card */}
        <div className="p-4 bg-slate-900 border border-slate-800 rounded-2xl space-y-2">
          <div className="flex justify-between items-center text-slate-400">
            <span className="font-semibold">Table Occupancy</span>
            <Users className="w-4 h-4 text-sky-400" />
          </div>
          <div className="text-2xl font-black text-white">{data.tables.occupancyPercent}%</div>
          <p className="text-[11px] text-slate-400">{data.tables.occupied} of {data.tables.total} Tables Active</p>
        </div>

        {/* Kitchen Load */}
        <div className="p-4 bg-slate-900 border border-slate-800 rounded-2xl space-y-2">
          <div className="flex justify-between items-center text-slate-400">
            <span className="font-semibold">Kitchen Load</span>
            <UtensilsCrossed className="w-4 h-4 text-amber-400" />
          </div>
          <div className="text-2xl font-black text-amber-400">{data.kitchen.kitchenLoadPercent}%</div>
          <p className="text-[11px] text-slate-400">{data.kitchen.activeOrdersCount} Tickets in Prep</p>
        </div>

        {/* Waitlist Queue */}
        <div className="p-4 bg-slate-900 border border-slate-800 rounded-2xl space-y-2">
          <div className="flex justify-between items-center text-slate-400">
            <span className="font-semibold">Live Waitlist</span>
            <Clock className="w-4 h-4 text-purple-400" />
          </div>
          <div className="text-2xl font-black text-purple-300">{data.todayMetrics.waitlistQueueLength} Groups</div>
          <p className="text-[11px] text-slate-400">Avg {data.todayMetrics.avgWaitTimeMins} mins wait</p>
        </div>

        {/* Parking Utilization */}
        <div className="p-4 bg-slate-900 border border-slate-800 rounded-2xl space-y-2">
          <div className="flex justify-between items-center text-slate-400">
            <span className="font-semibold">Parking Lot</span>
            <TrendingUp className="w-4 h-4 text-emerald-400" />
          </div>
          <div className="text-2xl font-black text-emerald-400">{data.parking.utilizationPercent}%</div>
          <p className="text-[11px] text-slate-400">{data.parking.occupiedSlots} of {data.parking.totalSlots} Slots Full</p>
        </div>
      </div>

      {/* Secondary Metrics Bar */}
      <div className="grid grid-cols-3 gap-4 text-xs bg-slate-900/60 p-4 rounded-2xl border border-slate-800">
        <div>
          <span className="text-slate-400 font-medium">Reservations & Walk-Ins</span>
          <p className="text-base font-extrabold text-white mt-1">
            {data.todayMetrics.reservationsCount} Bookings / {data.todayMetrics.walkInsCount} Walk-ins
          </p>
        </div>
        <div>
          <span className="text-slate-400 font-medium">Average Dining Time</span>
          <p className="text-base font-extrabold text-white mt-1">{data.todayMetrics.avgDiningTimeMins} Minutes</p>
        </div>
        <div>
          <span className="text-slate-400 font-medium">No-Show & Cancellation %</span>
          <p className="text-base font-extrabold text-white mt-1">
            {data.todayMetrics.noShowRate}% No-show | {data.todayMetrics.cancellationRate}% Cancelled
          </p>
        </div>
      </div>
    </div>
  );
};
