import React, { useState } from 'react';
import { Users, Calculator, DollarSign, Check, Plus, Trash2, PieChart, Receipt } from 'lucide-react';

export interface OrderItem {
  id: string;
  menuItemId: string;
  itemName: string;
  unitPrice: number;
  quantity: number;
}

export interface AttendeeAssignment {
  attendeeName: string;
  itemIds: string[];
}

export interface BillShare {
  id: string;
  attendeeName: string;
  subtotal: number;
  tax: number;
  tip: number;
  totalShare: number;
  isPaid: boolean;
}

export interface BillSplitResult {
  id: string;
  splitType: 'EQUAL' | 'ITEMIZED';
  numAttendees: number;
  tipPercent: number;
  subtotal: number;
  tax: number;
  tip: number;
  grandTotal: number;
  shares: BillShare[];
}

interface SplitBillComponentProps {
  sessionId: string;
  orders: OrderItem[];
  apiBaseUrl?: string;
  authToken?: string;
  onSplitComplete?: (split: BillSplitResult) => void;
}

export const SplitBillComponent: React.FC<SplitBillComponentProps> = ({
  sessionId,
  orders,
  apiBaseUrl = 'http://localhost:3000',
  authToken,
  onSplitComplete,
}) => {
  const [splitType, setSplitType] = useState<'EQUAL' | 'ITEMIZED'>('EQUAL');
  const [numAttendeesEqual, setNumAttendeesEqual] = useState<number>(2);
  const [tipPercent, setTipPercent] = useState<number>(15);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Itemized Attendees
  const [attendees, setAttendees] = useState<AttendeeAssignment[]>([
    { attendeeName: 'Guest 1', itemIds: [] },
    { attendeeName: 'Guest 2', itemIds: [] },
  ]);

  const [splitResult, setSplitResult] = useState<BillSplitResult | null>(null);

  // Calculate Subtotal from orders
  const sessionSubtotal = orders.reduce((sum, item) => sum + item.unitPrice * item.quantity, 0);

  const handleAddAttendee = () => {
    setAttendees((prev) => [
      ...prev,
      { attendeeName: `Guest ${prev.length + 1}`, itemIds: [] },
    ]);
  };

  const handleRemoveAttendee = (index: number) => {
    if (attendees.length <= 1) return;
    setAttendees((prev) => prev.filter((_, i) => i !== index));
  };

  const handleAttendeeNameChange = (index: number, name: string) => {
    setAttendees((prev) => {
      const updated = [...prev];
      updated[index].attendeeName = name;
      return updated;
    });
  };

  const handleToggleItemForAttendee = (attendeeIndex: number, itemId: string) => {
    setAttendees((prev) => {
      const updated = [...prev];
      const currentItems = updated[attendeeIndex].itemIds;
      if (currentItems.includes(itemId)) {
        updated[attendeeIndex].itemIds = currentItems.filter((id) => id !== itemId);
      } else {
        updated[attendeeIndex].itemIds = [...currentItems, itemId];
      }
      return updated;
    });
  };

  const handleCalculateSplit = async () => {
    setIsSubmitting(true);
    setError(null);

    try {
      const payload = {
        splitType,
        numAttendees: splitType === 'EQUAL' ? numAttendeesEqual : attendees.length,
        tipPercent,
        attendees: splitType === 'ITEMIZED' ? attendees : undefined,
      };

      const res = await fetch(`${apiBaseUrl}/api/v1/qr-dining/session/${sessionId}/split`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...(authToken ? { Authorization: `Bearer ${authToken}` } : {}),
        },
        body: JSON.stringify(payload),
      });

      if (!res.ok) {
        const err = await res.json();
        throw new Error(err.message || 'Failed to calculate split bill');
      }

      const result: BillSplitResult = await res.json();
      setSplitResult(result);
      onSplitComplete?.(result);
    } catch (err: any) {
      setError(err.message || 'An error occurred during calculation');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handlePayShare = async (shareId: string) => {
    try {
      const res = await fetch(`${apiBaseUrl}/api/v1/qr-dining/share/${shareId}/pay`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...(authToken ? { Authorization: `Bearer ${authToken}` } : {}),
        },
      });

      if (!res.ok) {
        const err = await res.json();
        throw new Error(err.message || 'Failed to process share payment');
      }

      // Update local state
      setSplitResult((prev) => {
        if (!prev) return null;
        return {
          ...prev,
          shares: prev.shares.map((s) => (s.id === shareId ? { ...s, isPaid: true } : s)),
        };
      });
    } catch (err: any) {
      setError(err.message);
    }
  };

  return (
    <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 text-slate-100 shadow-xl max-w-xl w-full">
      {/* Header */}
      <div className="flex items-center justify-between border-b border-slate-800 pb-4 mb-5">
        <div className="flex items-center gap-3">
          <span className="p-2.5 bg-amber-500/10 text-amber-400 rounded-xl">
            <PieChart className="w-6 h-6" />
          </span>
          <div>
            <h3 className="text-lg font-bold text-white">Split Table Bill</h3>
            <p className="text-xs text-slate-400">Equal split or itemized selection per guest</p>
          </div>
        </div>
        <div className="text-right">
          <span className="block text-[10px] uppercase font-semibold text-slate-400">Subtotal</span>
          <span className="text-xl font-extrabold text-amber-400">${sessionSubtotal.toFixed(2)}</span>
        </div>
      </div>

      {error && (
        <div className="p-3 bg-rose-500/10 border border-rose-500/20 text-rose-400 text-xs rounded-xl mb-4">
          {error}
        </div>
      )}

      {!splitResult ? (
        <div className="space-y-5">
          {/* Split Type Selector */}
          <div>
            <label className="block text-xs font-semibold uppercase text-slate-400 mb-2">Split Mode</label>
            <div className="grid grid-cols-2 gap-3">
              <button
                type="button"
                onClick={() => setSplitType('EQUAL')}
                className={`p-3 rounded-xl border text-sm font-semibold flex items-center justify-center gap-2 transition-all ${
                  splitType === 'EQUAL'
                    ? 'bg-amber-500 border-amber-400 text-slate-950 font-bold shadow-lg shadow-amber-500/20'
                    : 'bg-slate-800/60 border-slate-700 text-slate-300 hover:border-slate-600'
                }`}
              >
                <Users className="w-4 h-4" />
                <span>Split Equally</span>
              </button>
              <button
                type="button"
                onClick={() => setSplitType('ITEMIZED')}
                className={`p-3 rounded-xl border text-sm font-semibold flex items-center justify-center gap-2 transition-all ${
                  splitType === 'ITEMIZED'
                    ? 'bg-amber-500 border-amber-400 text-slate-950 font-bold shadow-lg shadow-amber-500/20'
                    : 'bg-slate-800/60 border-slate-700 text-slate-300 hover:border-slate-600'
                }`}
              >
                <Calculator className="w-4 h-4" />
                <span>Split by Selected Items</span>
              </button>
            </div>
          </div>

          {/* Tip Selection */}
          <div>
            <label className="block text-xs font-semibold uppercase text-slate-400 mb-2">Tip Percentage</label>
            <div className="grid grid-cols-4 gap-2">
              {[10, 15, 18, 20].map((pct) => (
                <button
                  key={pct}
                  type="button"
                  onClick={() => setTipPercent(pct)}
                  className={`py-2 rounded-lg text-xs font-bold transition-all border ${
                    tipPercent === pct
                      ? 'bg-slate-100 text-slate-950 border-white'
                      : 'bg-slate-800 border-slate-700 text-slate-300 hover:border-slate-600'
                  }`}
                >
                  {pct}%
                </button>
              ))}
            </div>
          </div>

          {/* Equal Split Options */}
          {splitType === 'EQUAL' && (
            <div>
              <label className="block text-xs font-semibold uppercase text-slate-400 mb-2">
                Number of Guests
              </label>
              <div className="flex items-center gap-3">
                {[2, 3, 4, 5, 6].map((count) => (
                  <button
                    key={count}
                    type="button"
                    onClick={() => setNumAttendeesEqual(count)}
                    className={`flex-1 py-2.5 rounded-xl border font-bold text-sm transition-all ${
                      numAttendeesEqual === count
                        ? 'bg-amber-500/20 border-amber-500 text-amber-300'
                        : 'bg-slate-800 border-slate-700 text-slate-300'
                    }`}
                  >
                    {count} Guests
                  </button>
                ))}
              </div>
            </div>
          )}

          {/* Itemized Split Options */}
          {splitType === 'ITEMIZED' && (
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <label className="text-xs font-semibold uppercase text-slate-400">Guest Item Assignments</label>
                <button
                  type="button"
                  onClick={handleAddAttendee}
                  className="text-xs font-bold text-amber-400 hover:text-amber-300 flex items-center gap-1"
                >
                  <Plus className="w-3.5 h-3.5" />
                  <span>Add Guest</span>
                </button>
              </div>

              {attendees.map((att, index) => (
                <div key={index} className="p-4 bg-slate-800/60 border border-slate-700/60 rounded-xl space-y-3">
                  <div className="flex items-center gap-2">
                    <input
                      type="text"
                      value={att.attendeeName}
                      onChange={(e) => handleAttendeeNameChange(index, e.target.value)}
                      className="bg-slate-900 border border-slate-700 rounded-lg px-3 py-1.5 text-xs font-bold text-white focus:outline-none focus:border-amber-500"
                    />
                    {attendees.length > 1 && (
                      <button
                        type="button"
                        onClick={() => handleRemoveAttendee(index)}
                        className="p-1 text-slate-400 hover:text-rose-400 transition-colors ml-auto"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    )}
                  </div>

                  {/* Order Items Checkboxes */}
                  <div className="grid grid-cols-2 gap-2">
                    {orders.map((item) => {
                      const isSelected = att.itemIds.includes(item.id);
                      return (
                        <button
                          key={item.id}
                          type="button"
                          onClick={() => handleToggleItemForAttendee(index, item.id)}
                          className={`p-2 rounded-lg text-left text-xs border transition-all ${
                            isSelected
                              ? 'bg-amber-500/20 border-amber-500 text-amber-200'
                              : 'bg-slate-900/60 border-slate-800 text-slate-400 hover:border-slate-700'
                          }`}
                        >
                          <div className="font-semibold text-white">{item.itemName}</div>
                          <div className="text-[10px] text-slate-400">
                            ${(item.unitPrice * item.quantity).toFixed(2)}
                          </div>
                        </button>
                      );
                    })}
                  </div>
                </div>
              ))}
            </div>
          )}

          <button
            type="button"
            onClick={handleCalculateSplit}
            disabled={isSubmitting}
            className="w-full py-3 bg-amber-500 hover:bg-amber-400 text-slate-950 font-bold text-sm rounded-xl shadow-lg shadow-amber-500/20 transition-all flex items-center justify-center gap-2"
          >
            <Receipt className="w-4 h-4" />
            <span>{isSubmitting ? 'Calculating...' : 'Calculate & Split Bill'}</span>
          </button>
        </div>
      ) : (
        /* Results View */
        <div className="space-y-4">
          <div className="p-4 bg-slate-800/80 rounded-xl border border-slate-700/60 flex justify-between text-xs text-slate-300">
            <div>
              <span className="block text-slate-400">Subtotal: ${splitResult.subtotal.toFixed(2)}</span>
              <span className="block text-slate-400">Tax (8%): ${splitResult.tax.toFixed(2)}</span>
            </div>
            <div className="text-right">
              <span className="block text-slate-400">Tip ({splitResult.tipPercent}%): ${splitResult.tip.toFixed(2)}</span>
              <span className="block font-bold text-amber-400 text-sm mt-0.5">Grand Total: ${splitResult.grandTotal.toFixed(2)}</span>
            </div>
          </div>

          <div className="space-y-3">
            <h4 className="text-xs font-semibold uppercase text-slate-400">Attendee Shares</h4>
            {splitResult.shares.map((share) => (
              <div
                key={share.id}
                className="p-4 bg-slate-800 border border-slate-700 rounded-xl flex items-center justify-between"
              >
                <div>
                  <h5 className="text-sm font-bold text-white">{share.attendeeName}</h5>
                  <p className="text-xs text-slate-400">
                    Base: ${share.subtotal.toFixed(2)} + Tax: ${share.tax.toFixed(2)} + Tip: ${share.tip.toFixed(2)}
                  </p>
                </div>
                <div className="flex items-center gap-3">
                  <span className="text-lg font-extrabold text-amber-400">${share.totalShare.toFixed(2)}</span>
                  {share.isPaid ? (
                    <span className="px-2.5 py-1 bg-emerald-500/20 text-emerald-400 rounded-lg text-xs font-bold flex items-center gap-1">
                      <Check className="w-3.5 h-3.5" />
                      Paid
                    </span>
                  ) : (
                    <button
                      type="button"
                      onClick={() => handlePayShare(share.id)}
                      className="px-3 py-1.5 bg-amber-500 hover:bg-amber-400 text-slate-950 font-bold text-xs rounded-lg transition-colors"
                    >
                      Pay Share
                    </button>
                  )}
                </div>
              </div>
            ))}
          </div>

          <button
            type="button"
            onClick={() => setSplitResult(null)}
            className="w-full py-2.5 border border-slate-700 text-slate-300 hover:bg-slate-800 rounded-xl text-xs font-semibold transition-colors"
          >
            Recalculate Split
          </button>
        </div>
      )}
    </div>
  );
};
