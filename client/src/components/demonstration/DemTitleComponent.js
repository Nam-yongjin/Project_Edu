const DemTitleComponent = ({title})=> {
    return (
            <div className="w-screen max-w-full">
      <h1 className="text-3xl font-bold mb-2 pl-[200px] mt-[80px]">{title}</h1>
      <hr className="w-4/5 mx-auto border-t-2 border-gray-400" />
    </div>
    );
};
export default DemTitleComponent;