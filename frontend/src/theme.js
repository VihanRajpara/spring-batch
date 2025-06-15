// src/theme.js
import { createTheme } from '@mui/material/styles';

const theme = createTheme({
  components: {
    MuiCssBaseline: {
      styleOverrides: {
        body: {
          fontFamily: `'Poppins', sans-serif`,
        },
        '*': {
          fontFamily: `'Poppins', sans-serif !important`,
        },
      },
    },
  },
});

export default theme;
