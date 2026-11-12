import { create } from 'zustand';

/** Toasts, confirm boxes and the few dialogs that can be opened from anywhere. */

export interface Toast {
  id: number;
  kind: 'success' | 'failure';
  message: string;
}

export interface ConfirmOptions {
  title?: string;
  message: string;
  positive?: string;
  /** Omit for a plain "ok" alert. */
  negative?: string;
}

interface ConfirmRequest extends ConfirmOptions {
  resolve: (ok: boolean) => void;
}

interface UiState {
  toasts: Toast[];
  confirm: ConfirmRequest | null;
  cityPickerOpen: boolean;
  adTypeOpen: boolean;
  rulesOpen: boolean;
  contactUsOpen: boolean;
  notificationsOpen: boolean;

  pushToast: (kind: Toast['kind'], message: string) => void;
  dropToast: (id: number) => void;
  askConfirm: (options: ConfirmOptions) => Promise<boolean>;
  answerConfirm: (ok: boolean) => void;
  set: (patch: Partial<Pick<UiState, 'cityPickerOpen' | 'adTypeOpen' | 'rulesOpen' | 'contactUsOpen' | 'notificationsOpen'>>) => void;
}

let nextToastId = 1;

export const useUi = create<UiState>()((set, get) => ({
  toasts: [],
  confirm: null,
  cityPickerOpen: false,
  adTypeOpen: false,
  rulesOpen: false,
  contactUsOpen: false,
  notificationsOpen: false,

  pushToast: (kind, message) => {
    const id = nextToastId++;
    // One toast at a time, like Android: a new one replaces the old.
    set({ toasts: [{ id, kind, message }] });
    window.setTimeout(() => get().dropToast(id), kind === 'failure' ? 3800 : 3000);
  },
  dropToast: (id) => set((state) => ({ toasts: state.toasts.filter((t) => t.id !== id) })),

  askConfirm: (options) =>
    new Promise<boolean>((resolve) => {
      get().confirm?.resolve(false);
      set({ confirm: { ...options, resolve } });
    }),
  answerConfirm: (ok) => {
    get().confirm?.resolve(ok);
    set({ confirm: null });
  },

  set: (patch) => set(patch),
}));

export const toast = {
  success: (message: string) => useUi.getState().pushToast('success', message),
  failure: (message: string) => useUi.getState().pushToast('failure', message),
};

export const confirmDialog = (options: ConfirmOptions) => useUi.getState().askConfirm(options);
export const alertDialog = (message: string, positive = 'باشه', title?: string) =>
  useUi.getState().askConfirm({ message, positive, title });

export const openCityPicker = () => useUi.getState().set({ cityPickerOpen: true });
export const openAdTypeDialog = () => useUi.getState().set({ adTypeOpen: true });
export const openRules = () => useUi.getState().set({ rulesOpen: true });
export const openContactUs = () => useUi.getState().set({ contactUsOpen: true });
export const openNotifications = () => useUi.getState().set({ notificationsOpen: true });
