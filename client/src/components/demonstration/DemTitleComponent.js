const DemTitleComponent = ({ title }) => {
  return (
    <div className="w-4/5 mt-4 mb-8 px-4 ml-[270px]">
      <h1 className="text-3xl font-bold mb-2">{title}</h1>
      <hr className="w-full border-t-2 border-gray-300" />
    </div>
  );
};
export default DemTitleComponent;