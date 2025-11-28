/** @type {import('tailwindcss').Config} */
export default {
  darkMode: 'class',

  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        'brand-primary': {
          DEFAULT: '#2563EB',
          'light': '#60A5FA',
          'dark': '#1D4ED8',
        },
        'brand-accent': {
          DEFAULT: '#F97316',
          'hover': '#EA580C',
        },
        'logo-light': '#0EA5E9',
      }
    },
  },
  plugins: [],
}