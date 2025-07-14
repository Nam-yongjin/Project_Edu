import './App.css';
import { RouterProvider } from 'react-router-dom';
import root from './router/root';

function App() {
  return (

    <RouterProvider router={root}>
      <div className='text-center'>GitHub Page Test</div>
    </RouterProvider>

  );
}

export default App;
