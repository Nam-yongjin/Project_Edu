import MainMenu from "../menus/MainMenu"
import Header from "../layouts/Header"
import Footer from "../layouts/Footer"

const MainPage = () => {
    return (
        <div className=" text-3xl">
            <div><MainMenu /></div>
            <div><Header /></div>
            <div>Main Page</div>
            <div><Footer /></div>
        </div>
    )
}

export default MainPage
