import { ErrorModel } from "../model/error-model";

interface ErrorState {
    error?: ErrorModel;
    visible: boolean;
}

interface Action {
    type: string,
    payload: ErrorState
}

export const initErrorState: ErrorState = { error: undefined, visible: false };

export const SHOW_ERROR = "ERROR_STATE/SHOW_ERROR";
export const HIDE_ERROR = "ERROR_STATE/HIDE_ERROR";


export function showError(error: ErrorModel): Action {
    return {
        type: SHOW_ERROR,
        payload: {
            error,
            visible: true
        }
    }
}

export function hideError(): Action {
    return {
        type: HIDE_ERROR,
        payload: {
            error: undefined,
            visible: false
        }
    }
}

export const errorState = (state: ErrorState = initErrorState, action: Action): ErrorState => {
    switch (action.type) {
        case SHOW_ERROR:
        case HIDE_ERROR:
            return { ...state, ...action.payload }
        default:
            return state;
    }
}