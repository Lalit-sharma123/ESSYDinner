import React from 'react';
import { AlertOctagon, CheckCircle2, ShieldAlert, HeartPulse } from 'lucide-react';

export interface ConflictingItem {
  menuItemId: string;
  itemName: string;
  matchedAllergens: string[];
}

interface AllergyWarningBannerProps {
  safe: boolean;
  warnings: ConflictingItem[];
  userAllergies: string[];
  onConfirmOverride?: () => void;
}

export const AllergyWarningBanner: React.FC<AllergyWarningBannerProps> = ({
  safe,
  warnings,
  userAllergies,
  onConfirmOverride,
}) => {
  if (safe) {
    return (
      <div className="p-4 bg-emerald-500/10 border border-emerald-500/30 rounded-2xl flex items-center gap-3 text-emerald-300 text-xs font-semibold">
        <CheckCircle2 className="w-5 h-5 text-emerald-400 shrink-0" />
        <div>
          <p className="font-bold text-white">Allergen Safety Verified</p>
          <p className="text-emerald-400/80">No conflicts with your registered health allergies ({userAllergies.join(', ') || 'None'}).</p>
        </div>
      </div>
    );
  }

  return (
    <div className="p-5 bg-rose-950/80 border-2 border-rose-500 rounded-2xl text-slate-100 shadow-2xl space-y-4 animate-shake">
      <div className="flex items-start gap-3">
        <span className="p-2.5 bg-rose-500/20 text-rose-400 rounded-xl shrink-0">
          <AlertOctagon className="w-6 h-6" />
        </span>
        <div>
          <h4 className="text-base font-extrabold text-white flex items-center gap-2">
            ALLERGY WARNING REQUIRED ACKNOWLEDGEMENT
          </h4>
          <p className="text-xs text-rose-300/90 mt-0.5">
            The following menu items contain ingredients matching your registered allergy profiles:
          </p>
        </div>
      </div>

      <div className="space-y-2 bg-slate-900/90 p-3.5 rounded-xl border border-rose-500/30">
        {warnings.map((w) => (
          <div key={w.menuItemId} className="flex items-center justify-between text-xs">
            <span className="font-bold text-white">{w.itemName}</span>
            <div className="flex items-center gap-1">
              {w.matchedAllergens.map((alg) => (
                <span
                  key={alg}
                  className="px-2 py-0.5 bg-rose-500/20 border border-rose-500/40 text-rose-300 font-extrabold rounded-full text-[10px]"
                >
                  ⚠ {alg}
                </span>
              ))}
            </div>
          </div>
        ))}
      </div>

      <div className="flex items-center justify-between pt-2 border-t border-rose-500/20">
        <p className="text-[11px] text-slate-400">Audit logs record user allergen confirmation.</p>
        <button
          onClick={onConfirmOverride}
          className="px-4 py-2 bg-rose-600 hover:bg-rose-500 text-white font-extrabold text-xs rounded-xl shadow-lg transition-colors"
        >
          Confirm & Override Warning
        </button>
      </div>
    </div>
  );
};
