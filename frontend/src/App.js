import logo from './logo.svg';
import './App.css';
import { Router } from './components/utils/Router';
import { Toaster } from 'react-hot-toast';

function App() {
  return (
    <>
      <Toaster/>
      <Router/>
    </>
  );
}

export default App;
