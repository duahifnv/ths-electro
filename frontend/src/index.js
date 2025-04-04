import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import './react-envelope/variables.css'
import './react-envelope/styles.css'
import './variables.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import { AuthProvider } from './react-envelope/contexts/AuthContext';
import { AccountsProvider } from './react-envelope/contexts/AccountsContext';
import { ThemeProvider } from './react-envelope/contexts/ThemeContext';
import { NavigationProvider } from './react-envelope/contexts/NavigationContext';
import { SyntaxHighlighterProvider } from './react-envelope/contexts/SyntaxHighlighterContext';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <ThemeProvider>
      <SyntaxHighlighterProvider>
        <AuthProvider>
          <AccountsProvider>
            <NavigationProvider>
              <App/>
            </NavigationProvider>
          </AccountsProvider>
        </AuthProvider>
      </SyntaxHighlighterProvider>
    </ThemeProvider>
  </React.StrictMode>
);

reportWebVitals();
