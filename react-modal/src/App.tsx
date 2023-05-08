import {useDispatch, useSelector} from 'react-redux';
import './App.css';
import {RootState} from './reducer';
import {closeModal, openModal, setModal} from './reducer/modal';

function Modal ({visible, subComponent}: {visible: boolean, subComponent?: JSX.Element}) {
    if (visible && subComponent) { 
        return subComponent;
    } else {
        return null;
    }
}

function SampleComponent () {
    return <div>Hello World</div>
}

function App() {
    const modal = useSelector((state: RootState) => state.modal);
    const dispatch = useDispatch();

    function handleModalToggle() {
        if (modal.visible) {
            dispatch(closeModal());
        } else {
            dispatch(setModal(<SampleComponent />))
            dispatch(openModal());
        }
    }

    return (
        <div>
            <button onClick={handleModalToggle}>Modal Toggle</button>
            <Modal visible={modal.visible} subComponent={modal.component} />
        </div>
    );
}

export default App;
