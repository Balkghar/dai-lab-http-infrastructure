module.exports = {
    content: ['./views/*.ejs', './app.js', './public/*.{js,css}'], darkMode: 'class', theme: {
        extend: {},
    }, plugins: [{
        tailwindcss: {}, autoprefixer: {},
    },],
};