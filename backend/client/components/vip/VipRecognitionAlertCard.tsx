import React from 'react';
import { Crown, Heart, Sparkles, AlertTriangle, Utensils, Star, Calendar } from 'lucide-react';

export interface VipAlertData {
  userId: string;
  customerName: string;
  email: string;
  phone: string;
  tier: string;
  visitCount: number;
  lifetimeSpend: number;
  tableNumber: string;
  favoriteDish: string;
  favoriteTable: string;
  allergyAlerts: string[];
  specialRequests: string;
}

interface VipRecognitionAlertCardProps {
  vipAlert?: VipAlertData;
  onDismiss?: () => void;
}

export const VipRecognitionAlertCard: React.FC<VipRecognitionAlertCardProps> = ({ vipAlert, onDismiss }) => {
  const data: VipAlertData = vipAlert || {
    userId: 'u_1',
    customerName: 'Sarah Jenkins',
    email: 'sarah.j@example.com',
    phone: '+1 555-0198',
    tier: 'PLATINUM_VIP',
    visitCount: 18,
    lifetimeSpend: 2450.0,
    tableNumber: 'Booth #4',
    favoriteDish: 'Truffle Tagliatelle & Wagyu Ribeye',
    favoriteTable: 'Window Booth #4',
    allergyAlerts: ['PEANUTS', 'SHELLFISH'],
    specialRequests: 'Prefers sparkling water upon arrival, celebrate anniversary',
  };

  return (
    <div className="bg-gradient-to-br from-amber-950/90 via-slate-900 to-slate-950 border-2 border-amber-500/60 rounded-2xl p-6 text-slate-100 shadow-2xl max-w-lg w-full space-y-4 animate-bounce-short">
      {/* Top Banner */}
      <div className="flex items-center justify-between border-b border-amber-500/30 pb-3">
        <div className="flex items-center gap-3">
          <span className="p-2.5 bg-amber-500/20 text-amber-300 rounded-xl border border-amber-500/40 shadow-amber-500/20 shadow-lg">
            <Crown className="w-6 h-6" />
          </span>
          <div>
            <div className="flex items-center gap-2">
              <h3 className="text-lg font-black text-white">{data.customerName}</h3>
              <span className="px-2 py-0.5 bg-amber-500 text-slate-950 font-black text-[10px] rounded-full uppercase">
                {data.tier}
              </span>
            </div>
            <p className="text-xs text-amber-300/80 font-medium">VIP Check-In Detected at {data.tableNumber}</p>
          </div>
        </div>

        <button
          onClick={onDismiss}
          className="px-2.5 py-1 bg-slate-800 hover:bg-slate-700 text-slate-400 hover:text-white rounded-lg text-xs"
        >
          Dismiss
        </button>
      </div>

      {/* Stats row */}
      <div className="grid grid-cols-2 gap-3 text-xs">
        <div className="p-3 bg-slate-900/80 border border-amber-500/20 rounded-xl">
          <span className="text-slate-400 font-medium">Lifetime Spend</span>
          <p className="text-base font-black text-emerald-400">${data.lifetimeSpend.toLocaleString()}</p>
        </div>
        <div className="p-3 bg-slate-900/80 border border-amber-500/20 rounded-xl">
          <span className="text-slate-400 font-medium">Total Visits</span>
          <p className="text-base font-black text-amber-400">{data.visitCount} Visits</p>
        </div>
      </div>

      {/* Preferences & Allergy Details */}
      <div className="space-y-2 text-xs">
        <div className="flex items-start gap-2 text-slate-300">
          <Utensils className="w-4 h-4 text-amber-400 shrink-0 mt-0.5" />
          <div>
            <span className="font-bold text-white">Favorite Dishes:</span> {data.favoriteDish}
          </div>
        </div>

        {data.allergyAlerts.length > 0 && (
          <div className="p-2.5 bg-rose-500/10 border border-rose-500/30 rounded-xl flex items-center gap-2 text-rose-300 font-bold">
            <AlertTriangle className="w-4 h-4 text-rose-400 shrink-0" />
            <span>Allergy Alerts: {data.allergyAlerts.join(', ')}</span>
          </div>
        )}

        <div className="p-3 bg-amber-500/10 border border-amber-500/20 rounded-xl text-amber-200">
          <span className="font-extrabold text-amber-400 block mb-0.5">Staff Service Requests:</span>
          {data.specialRequests}
        </div>
      </div>
    </div>
  );
};
