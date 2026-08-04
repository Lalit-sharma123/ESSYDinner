import React, { useState } from 'react';
import { BarChart3, TrendingUp, Clock, AlertTriangle } from 'lucide-react';

export interface CrowdHourData {
  hourOfDay: number;
  occupancyRate: number;
  crowdLevel: string;
}

interface CrowdHeatmapCardProps {
  restaurantId: string;
  heatmapData?: CrowdHourData[];
}

export const CrowdHeatmapCard: React.FC<CrowdHeatmapCardProps> = ({ heatmapData }) => {
  const [selectedDay, setSelectedDay] = useState(1); // Mon=1

  const defaultData: CrowdHourData[] = Array.from({ length: 24 }).map((_, h) => {
    let rate = 15;
    if (h >= 12 && h <= 14) rate = 80;
    if (h >= 18 && h <= 21) rate = 95;
    if (h >= 15 && h <= 17) rate = 40;
    let crowd = 'Quiet';
    if (rate > 35) crowd = 'Moderate';
    if (rate > 75) crowd = 'Busy';
    if (rate > 90) crowd = 'Very Busy';

    return {
      hourOfDay: h,
      occupancyRate: rate,
      crowdLevel: crowd,
    };
  });

  const hours = heatmapData && heatmapData.length > 0 ? heatmapData : defaultData;

  const getHeatmapColor = (rate: number) => {
    if (rate < 25) return 'bg-emerald-500/80 hover:bg-emerald-400';
    if (rate < 50) return 'bg-sky-500/80 hover:bg-sky-400';
    if (rate < 75) return 'bg-amber-500/80 hover:bg-amber-400';
    if (rate < 90) return 'bg-orange-500/80 hover:bg-orange-400';
    return 'bg-rose-500/90 hover:bg-rose-400 animate-pulse';
  };

  return (
    <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 text-slate-100 shadow-xl max-w-2xl w-full space-y-6">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-3">
          <span className="p-2.5 bg-amber-500/10 text-amber-400 rounded-xl">
            <BarChart3 className="w-6 h-6" />
          </span>
          <div>
            <h3 className="text-lg font-bold text-white">Live Crowd Heat Map</h3>
            <p className="text-xs text-slate-400">Historical & predicted hourly occupancy levels</p>
          </div>
        </div>

        <div className="flex items-center gap-1 bg-slate-800 p-1 rounded-xl border border-slate-700 text-xs">
          {['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'].map((d, i) => (
            <button
              key={d}
              onClick={() => setSelectedDay(i)}
              className={`px-2.5 py-1 rounded-lg font-bold transition-colors ${
                selectedDay === i ? 'bg-amber-500 text-slate-950' : 'text-slate-400 hover:text-white'
              }`}
            >
              {d}
            </button>
          ))}
        </div>
      </div>

      {/* 24-Hour Bar Visualizer */}
      <div className="space-y-2">
        <div className="h-40 flex items-end justify-between gap-1 bg-slate-950 p-4 rounded-xl border border-slate-800">
          {hours.map((item) => (
            <div key={item.hourOfDay} className="flex-1 flex flex-col items-center gap-1 group relative">
              {/* Tooltip */}
              <div className="absolute -top-10 hidden group-hover:flex flex-col items-center bg-slate-800 text-[10px] font-bold text-white px-2 py-1 rounded shadow-lg border border-slate-700 whitespace-nowrap z-10">
                <span>{item.hourOfDay}:00 - {item.occupancyRate}%</span>
                <span className="text-amber-400">{item.crowdLevel}</span>
              </div>

              <div
                style={{ height: `${Math.max(10, item.occupancyRate)}%` }}
                className={`w-full rounded-t transition-all ${getHeatmapColor(item.occupancyRate)}`}
              />
            </div>
          ))}
        </div>

        <div className="flex justify-between text-[10px] font-bold text-slate-500 px-2">
          <span>12 AM</span>
          <span>6 AM</span>
          <span>12 PM</span>
          <span>6 PM</span>
          <span>11 PM</span>
        </div>
      </div>

      {/* Recommended Times */}
      <div className="grid grid-cols-2 gap-4 text-xs">
        <div className="p-3 bg-emerald-500/10 border border-emerald-500/20 rounded-xl flex items-center gap-3">
          <Clock className="w-5 h-5 text-emerald-400 shrink-0" />
          <div>
            <p className="font-bold text-emerald-300">Best Quiet Hours</p>
            <p className="text-slate-400">15:00 - 17:00 (Very Quiet)</p>
          </div>
        </div>
        <div className="p-3 bg-rose-500/10 border border-rose-500/20 rounded-xl flex items-center gap-3">
          <TrendingUp className="w-5 h-5 text-rose-400 shrink-0" />
          <div>
            <p className="font-bold text-rose-300">Peak Busy Window</p>
            <p className="text-slate-400">19:00 - 21:00 (95% Full)</p>
          </div>
        </div>
      </div>
    </div>
  );
};
