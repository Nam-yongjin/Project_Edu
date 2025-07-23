import './App.css';
import { RouterProvider } from 'react-router-dom';
import root from './router/root';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
import { RecoilRoot } from 'recoil';

const queryClient = new QueryClient()

function App() {
  return (
    // {/* React Query 상태 관리(Caching, Fetch, Mutation 등) */}
    <QueryClientProvider client={queryClient}>

      <RouterProvider router={root} />

      {/* React Query의 개발자용 디버깅 도구 */}
      <ReactQueryDevtools initialIsOpen={true} />

    </QueryClientProvider>
  )
}

export default App
