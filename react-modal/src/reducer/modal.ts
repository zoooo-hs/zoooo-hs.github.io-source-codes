
interface ModalState {
    component?: JSX.Element;
    visible: boolean;
}

export const initModalState: ModalState = {component: undefined, visible: false};

export enum ModalAction {
    SET,
    OPEN,
    CLOSE
}

interface Action {
    type: ModalAction,
    payload: ModalState
}

export function setModal(component: JSX.Element): Action {
    return {
        type: ModalAction.SET,
        payload: {
            component,
            visible: false
        }
    }
}

export function openModal(): Action {
    return {
        type: ModalAction.OPEN,
        payload: {
            visible:true
        }
    }
}

export function closeModal(): Action {
    return {
        type: ModalAction.OPEN,
        payload: {
            visible:false
        }
    }
}


export const modal = (state:ModalState = initModalState, action: Action): ModalState => {
    switch(action.type) {
        case ModalAction.SET:
        case ModalAction.OPEN:
        case ModalAction.CLOSE:
            return {...state, ...action.payload}
        default:
            return state;
    }
}
