import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { Users, Sparkles, Clock, Utensils, AlertCircle } from 'lucide-react';

// Zod Schema Validation
const joinWaitlistSchema = z.object({
  restaurantId: z.string().min(1, 'Restaurant ID is required'),
  partySize: z.coerce.number().min(1, 'Party size must be at least 1 guest').max(12, 'For parties larger than 12, please contact restaurant directly'),
  seatingPreference: z.enum(['Indoor Dining', 'Outdoor Terrace', 'Bar High-Top', 'Window Booth']),
  specialNotes: z.string().optional(),
  isPriority: z.boolean().default(false),
});

export type JoinWaitlistFormValues = z.infer<typeof joinWaitlistSchema>;

interface JoinWaitlistDialogProps {
  isOpen: boolean;
  onClose: () => void;
  restaurantId: string;
  restaurantName: string;
  userTier?: 'VIP' | 'GOLD' | 'STANDARD';
  onSubmitSuccess: (data: any) => void;
  apiBaseUrl?: string;
  authToken?: string;
}

export const JoinWaitlistDialog: React.FC<JoinWaitlistDialogProps> = ({
  isOpen,
  onClose,
  restaurantId,
  restaurantName,
  userTier = 'STANDARD',
  onSubmitSuccess,
  apiBaseUrl = 'http://localhost:3000',
  authToken,
}) => {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [serverError, setServerError] = useState<string | null>(null);

  const isEligibleForPriority = userTier === 'VIP' || userTier === 'GOLD';

  const {
    register,
    handleSubmit,
    setValue,
    watch,
    formState: { errors },
    reset,
  } = useForm<JoinWaitlistFormValues>({
    resolver: zodResolver(joinWaitlistSchema),
    defaultValues: {
      restaurantId,
      partySize: 2,
      seatingPreference: 'Indoor Dining',
      specialNotes: '',
      isPriority: isEligibleForPriority,
    },
  });

  const watchPartySize = watch('partySize');
  const watchPriority = watch('isPriority');

  if (!isOpen) return null;

  const onSubmit = async (values: JoinWaitlistFormValues) => {
    setIsSubmitting(true);
    setServerError(null);

    try {
      const response = await fetch(`${apiBaseUrl}/api/v1/waitlist`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...(authToken ? { Authorization: `Bearer ${authToken}` } : {}),
        },
        body: JSON.stringify(values),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Failed to join waitlist queue');
      }

      const result = await response.json();
      onSubmitSuccess(result);
      reset();
      onClose();
    } catch (err: any) {
      setServerError(err.message || 'An unexpected error occurred');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4 animate-in fade-in duration-200">
      <div className="bg-slate-900 border border-slate-800 text-slate-100 rounded-2xl shadow-2xl max-w-lg w-full overflow-hidden transition-all">
        {/* Header */}
        <div className="px-6 pt-6 pb-4 border-b border-slate-800 flex items-start justify-between">
          <div>
            <div className="flex items-center gap-2">
              <span className="p-2 bg-amber-500/10 text-amber-400 rounded-lg">
                <Utensils className="w-5 h-5" />
              </span>
              <h2 className="text-xl font-bold tracking-tight text-white">Join Live Waitlist</h2>
            </div>
            <p className="text-sm text-slate-400 mt-1">
              Queue at <span className="font-semibold text-amber-400">{restaurantName}</span>
            </p>
          </div>
          <button
            onClick={onClose}
            className="text-slate-400 hover:text-white p-1 rounded-lg hover:bg-slate-800 transition-colors"
          >
            ✕
          </button>
        </div>

        {/* Form Body */}
        <form onSubmit={handleSubmit(onSubmit)} className="p-6 space-y-5">
          {serverError && (
            <div className="p-3 bg-rose-500/10 border border-rose-500/20 text-rose-400 rounded-xl flex items-center gap-2 text-sm">
              <AlertCircle className="w-4 h-4 shrink-0" />
              <span>{serverError}</span>
            </div>
          )}

          {/* Party Size Selector */}
          <div>
            <label className="block text-xs font-semibold uppercase tracking-wider text-slate-400 mb-2 flex items-center justify-between">
              <span>Party Size</span>
              <span className="text-amber-400 font-bold text-sm">{watchPartySize} Guests</span>
            </label>
            <div className="grid grid-cols-6 gap-2">
              {[1, 2, 3, 4, 5, 6].map((num) => (
                <button
                  key={num}
                  type="button"
                  onClick={() => setValue('partySize', num, { shouldValidate: true })}
                  className={`py-2.5 rounded-xl font-semibold text-sm transition-all border ${
                    watchPartySize === num
                      ? 'bg-amber-500 border-amber-400 text-slate-950 shadow-lg shadow-amber-500/20'
                      : 'bg-slate-800/60 border-slate-700 text-slate-300 hover:border-slate-600'
                  }`}
                >
                  {num}
                </button>
              ))}
            </div>
            {errors.partySize && (
              <p className="text-rose-400 text-xs mt-1">{errors.partySize.message}</p>
            )}
          </div>

          {/* Seating Preference */}
          <div>
            <label className="block text-xs font-semibold uppercase tracking-wider text-slate-400 mb-2">
              Seating Preference
            </label>
            <select
              {...register('seatingPreference')}
              className="w-full bg-slate-800 border border-slate-700 rounded-xl px-4 py-3 text-sm text-slate-100 focus:outline-none focus:border-amber-500 transition-colors"
            >
              <option value="Indoor Dining">Main Indoor Dining</option>
              <option value="Outdoor Terrace">Outdoor Terrace & Garden</option>
              <option value="Bar High-Top">Bar & Cocktail High-Top</option>
              <option value="Window Booth">Exclusive Window Booth</option>
            </select>
          </div>

          {/* Special Requests */}
          <div>
            <label className="block text-xs font-semibold uppercase tracking-wider text-slate-400 mb-2">
              Special Requests / Dietary Notes
            </label>
            <input
              type="text"
              placeholder="e.g. Highchair needed, Anniversary celebration, Peanut allergy"
              {...register('specialNotes')}
              className="w-full bg-slate-800 border border-slate-700 rounded-xl px-4 py-3 text-sm text-slate-100 focus:outline-none focus:border-amber-500 transition-colors placeholder:text-slate-500"
            />
          </div>

          {/* Priority Queue Toggle (VIP/Gold Upgrade) */}
          <div className="p-4 bg-amber-500/5 border border-amber-500/20 rounded-xl flex items-center justify-between">
            <div className="flex items-center gap-3">
              <span className="p-2 bg-amber-500/20 text-amber-400 rounded-lg">
                <Sparkles className="w-5 h-5" />
              </span>
              <div>
                <div className="flex items-center gap-2">
                  <h4 className="text-sm font-bold text-white">Priority Queue Jump</h4>
                  {isEligibleForPriority && (
                    <span className="px-2 py-0.5 text-[10px] font-extrabold uppercase bg-amber-400 text-slate-950 rounded-full">
                      {userTier} Perk
                    </span>
                  )}
                </div>
                <p className="text-xs text-slate-400">
                  {isEligibleForPriority
                    ? 'Complimentary priority placement applied via member status.'
                    : 'Upgrade to priority status for faster table seating.'}
                </p>
              </div>
            </div>
            <input
              type="checkbox"
              {...register('isPriority')}
              className="w-5 h-5 accent-amber-500 rounded cursor-pointer"
            />
          </div>

          {/* Actions */}
          <div className="pt-3 border-t border-slate-800 flex items-center justify-end gap-3">
            <button
              type="button"
              onClick={onClose}
              className="px-5 py-2.5 rounded-xl border border-slate-700 text-slate-300 hover:bg-slate-800 text-sm font-semibold transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isSubmitting}
              className="px-6 py-2.5 rounded-xl bg-amber-500 hover:bg-amber-400 text-slate-950 font-bold text-sm shadow-lg shadow-amber-500/20 disabled:opacity-50 transition-all flex items-center gap-2"
            >
              {isSubmitting ? (
                <span>Joining Queue...</span>
              ) : (
                <>
                  <Users className="w-4 h-4" />
                  <span>Confirm & Join Queue</span>
                </>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
